package com.nicico.internal.sales.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicico.copper.oauth.common.model.OAGroup;
import com.nicico.copper.oauth.common.model.OAPermission;
import com.nicico.copper.oauth.common.repository.OAGroupDAO;
import com.nicico.copper.oauth.common.repository.OAPermissionDAO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionsGroupRoleCreator {
	private final OAPermissionDAO permissionRepository;
	private final OAGroupDAO groupRepository;
	private final ObjectMapper objectMapper;
	/**
	 * Initializes default groups and their associated permissions for this application.
	 * <p>
	 * To update the default groups, simply modify both <code>permissions.json</code>
	 * and <code>permissions-group.json</code>. You can also provide these files to an AI tool
	 * and ask it to update the <code>permissions-group.json</code> file accordingly.
	 * </p>
	 */
	@Value("${spring.application.name}")
	private String appId;

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady() {
		try {
			Map<String, OAPermission> permissionMap = loadPermissions();
			GroupPermissionList groupPermissionList = readPermissionsFromFile();
			createGroupsIfMissing(groupPermissionList, permissionMap);
		} catch (Exception e) {
			log.error("Error while creating permissions/groups", e);
		}
	}

	private Map<String, OAPermission> loadPermissions() {
		Set<OAPermission> permissions = permissionRepository.findAllByAppId(appId);
		Map<String, OAPermission> map = new HashMap<>();
		for (OAPermission p : permissions) {
			map.put(p.getCode().toUpperCase(), p);
		}
		return map;
	}

	private GroupPermissionList readPermissionsFromFile() throws IOException {
		ClassPathResource resource = new ClassPathResource("permissions-group.json");
		return objectMapper.readValue(resource.getInputStream(), GroupPermissionList.class);
	}

	private void createGroupsIfMissing(GroupPermissionList groupPermissionList, Map<String, OAPermission> permissionMap) {
		List<String> existingGroupTitles = groupRepository.findAllByAppId(appId).stream().map(OAGroup::getTitle).toList();
		List<OAGroup> newGroups = new ArrayList<>();
		for (GroupPermission group : groupPermissionList.getList()) {
			if (group.getPermissions() == null) {
				log.warn("Skipping group '{}' - permissions list is null", group.getTitle());
				continue;
			}
			if (!existingGroupTitles.contains(group.getTitle())) {
				Set<OAPermission> groupPermissions = new HashSet<>();
				for (String p : group.getPermissions()) {
					OAPermission found = permissionMap.get(p.toUpperCase());
					if (found != null) {
						groupPermissions.add(found);
					} else {
						log.warn("Permission '{}' not found", p);
					}
				}
				if (groupPermissions.isEmpty()) continue;
				OAGroup oaGroup = new OAGroup();
				oaGroup.setTitle(group.getTitle());
				oaGroup.setAppId(appId);
				oaGroup.setPermissions(groupPermissions);
				newGroups.add(oaGroup);
			}
		}
		if (!newGroups.isEmpty()) {
			groupRepository.saveAll(newGroups);
			log.info("Created {} new groups", newGroups.size());
		} else {
			log.info("No new groups to create");
		}
	}
}

@Data
class GroupPermission {
	private String title;
	private String code;
	private List<String> permissions = new ArrayList<>();
}

@Data
class GroupPermissionList {
	private List<GroupPermission> list;
}
