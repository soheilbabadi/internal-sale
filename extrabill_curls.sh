#!/bin/bash

TOKEN="5dd10596-91ac-455c-ad66-7ea5e2fad5ac"
BASE_URL="http://localhost:8080"
AUTH_HEADER="Authorization: Bearer $TOKEN"

echo "=== ExtraBillController Test Commands ==="
echo ""

# 1. Search Issuable Bills
echo "1. Search Issuable Bills"
curl -X POST "$BASE_URL/api/v1/ins/extra-bill/search-issuable" \
  -H "Content-Type: application/json" \
  -H "$AUTH_HEADER" \
  -d '{}'
echo -e "\n"

# 2. Search Bills
echo "2. Search Bills"
curl -X POST "$BASE_URL/api/v1/ins/extra-bill/search" \
  -H "Content-Type: application/json" \
  -H "$AUTH_HEADER" \
  -d '{}'
echo -e "\n"

# 3. Search Issue History
echo "3. Search Issue History"
curl -X POST "$BASE_URL/api/v1/ins/extra-bill/search-issue-history" \
  -H "Content-Type: application/json" \
  -H "$AUTH_HEADER" \
  -d '{}'
echo -e "\n"

# 4. Get Bills by Master ID
echo "4. Get Bills by Master ID (10)"
curl -X GET "$BASE_URL/api/v1/ins/extra-bill/get-by-master/10" \
  -H "$AUTH_HEADER"
echo -e "\n"

# 5. Save New Bill
echo "5. Save New Bill"
curl -X POST "$BASE_URL/api/v1/ins/extra-bill/save" \
  -H "Content-Type: application/json" \
  -H "$AUTH_HEADER" \
  -d '{
    "proformaId": 1,
    "billNumber": "BILL123",
    "amount": 1000000,
    "dueDate": "2025-12-31"
  }'
echo -e "\n"

# 6. Save Multiple Bills
echo "6. Save Multiple Bills"
curl -X POST "$BASE_URL/api/v1/ins/extra-bill/save-all" \
  -H "Content-Type: application/json" \
  -H "$AUTH_HEADER" \
  -d '[
    {"proformaId": 1, "billNumber": "BILL123", "amount": 1000000},
    {"proformaId": 2, "billNumber": "BILL124", "amount": 2000000}
  ]'
echo -e "\n"

# 7. Update Bill Files
echo "7. Update Bill Files"
curl -X PUT "$BASE_URL/api/v1/ins/extra-bill/update-files" \
  -H "Content-Type: application/json" \
  -H "$AUTH_HEADER" \
  -d '{
    "extraBillId": 1,
    "extraBillFileId": 100,
    "dispatchAttachmentId": 200
  }'
echo -e "\n"

# 8. Send Reckoning Email Confirmation
echo "8. Send Reckoning Email Confirmation (ID: 1)"
curl -X POST "$BASE_URL/api/v1/ins/extra-bill/send-reckoning-extra-bill/1" \
  -H "$AUTH_HEADER"
echo -e "\n"

# 9. Update Bill Information
echo "9. Update Bill Information"
curl -X PUT "$BASE_URL/api/v1/ins/extra-bill/update" \
  -H "Content-Type: application/json" \
  -H "$AUTH_HEADER" \
  -d '{
    "extraBillId": 1,
    "bankCode": "001",
    "electronicCode": "ELC123456"
  }'
echo -e "\n"

# 10. Get Audit History
echo "10. Get Audit History (ID: 1)"
curl -X GET "$BASE_URL/api/v1/ins/extra-bill/audit-history/1" \
  -H "$AUTH_HEADER"
echo -e "\n"

# 11. Search Ready Reckoning Bills
echo "11. Search Ready Reckoning Bills"
curl -X POST "$BASE_URL/api/v1/ins/extra-bill/search/ready-reckoning" \
  -H "Content-Type: application/json" \
  -H "$AUTH_HEADER" \
  -d '{}'
echo -e "\n"

# 12. Get Workflow History Detail
echo "12. Get Workflow History Detail (ID: 1)"
curl -X GET "$BASE_URL/api/v1/ins/extra-bill/history/1" \
  -H "$AUTH_HEADER"
echo -e "\n"

# 13. Generate Broker Email Content
echo "13. Generate Broker Email Content (ID: 1)"
curl -X GET "$BASE_URL/api/v1/ins/extra-bill/get-broker-email-content/1" \
  -H "$AUTH_HEADER"
echo -e "\n"

# 14. Get User Tasks Report
echo "14. Get User Tasks Report (ID: 1)"
curl -X GET "$BASE_URL/api/v1/ins/extra-bill/user-tasks-report/1" \
  -H "$AUTH_HEADER"
echo -e "\n"

# 15. Cancel Extra Bill
echo "15. Cancel Extra Bill"
curl -X PUT "$BASE_URL/api/v1/ins/extra-bill/cancel-extrabill" \
  -H "Content-Type: application/json" \
  -H "$AUTH_HEADER" \
  -d '{
    "extraBillId": 1,
    "cancelReason": "Customer request"
  }'
echo -e "\n"

# 16. Update All LC Acknowledgments
echo "16. Update All LC Acknowledgments"
curl -X PUT "$BASE_URL/api/v1/ins/extra-bill/update-all-acknowledgment" \
  -H "$AUTH_HEADER"
echo -e "\n"

echo "=== All commands completed ==="
