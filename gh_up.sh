#!/bin/bash

# ./gh_up.sh "user/repo" "v1" "gh_token" "assets_name" "file/path/to/upload"
# REPO="${1:-user/repo}"
# TAG="${2:-v1}"
# TOKEN="${3:-gh_token}"
# ASSET_NAME="${4:-test.zip}"
# FILE_PATH="${5:-$ASSET_NAME}"

# Step 1: Get the Release ID
RELEASE_URL="https://api.github.com/repos/${REPO}/releases/tags/${TAG}"
RELEASE_ID=$(curl -s -H "Accept: application/vnd.github+json" -H "Authorization: token ${TOKEN}" "${RELEASE_URL}" | jq -r '.id')

if [[ -z "$RELEASE_ID" ]]; then
  echo "Error: Release ${TAG} not found."
  exit 1
fi

# Step 2: List existing assets
ASSETS_URL="https://api.github.com/repos/${REPO}/releases/${RELEASE_ID}/assets"
ASSETS=$(curl -s -H "Authorization: token ${TOKEN}" "${ASSETS_URL}")

# Step 3: Check if the asset exists and delete it
ASSET_ID=$(echo "$ASSETS" | jq --arg ASSET_NAME "$ASSET_NAME" -r '.[] | select(.name == $ASSET_NAME) | .id') 
if [[ -n "$ASSET_ID" ]]; then
    DELETE_URL="https://api.github.com/repos/${REPO}/releases/assets/${ASSET_ID}"
    curl -s -X DELETE -H "Authorization: token ${TOKEN}" "${DELETE_URL}"
    if [[ $? -ne 0 ]]; then
      echo "Error: Failed to delete existing asset ${ASSET_NAME}."
      exit 1
    fi
    echo "Existing asset ${ASSET_NAME} deleted."
fi

# Step 4: Upload the new asset
UPLOAD_URL="https://uploads.github.com/repos/${REPO}/releases/${RELEASE_ID}/assets?name=${ASSET_NAME}"
#curl -X POST -H "Authorization: token ${TOKEN}" -H "Content-Type: application/octet-stream" -F "file=@${FILE_PATH}" "${UPLOAD_URL}"
curl -X POST -H "Authorization: token ${TOKEN}" -H "Content-Type: application/octet-stream" --data-binary "@${FILE_PATH}" "${UPLOAD_URL}"

if [[ $? -eq 0 ]]; then
  echo "Asset ${ASSET_NAME} uploaded successfully."
else
  echo "Error uploading asset ${ASSET_NAME}."
fi
