#!/bin/sh
set -e

echo "Uploading: $IPA_PATH"
RELEASE_NOTES="Mode:$2 Build: $CIRCLE_BUILD_NUM | Circle CI URL: $CIRCLE_BUILD_URL | Branch: $CIRCLE_BRANCH | Commit: $CIRCLE_SHA1 | Tag $CIRCLE_TAG default"
echo "Release notes: $RELEASE_NOTES"

curl \
-F status="2" \
-F notify="$3" \
-F notes="$RELEASE_NOTES" \
-F notes_type="0" \
-F strategy="replace" \
-F ipa="@$1" \
-H "X-HockeyAppToken: $HOCKEY_APP_TOKEN" \
https://rink.hockeyapp.net/api/2/apps/upload

echo "\nHockeyapp upload complete."
