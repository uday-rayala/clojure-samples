pushd .

TODAY=`date "+%b %d 00:00:00 %Y"`

rm -rf $TMP_LOGS_DIR
mkdir $TMP_LOGS_DIR

echo "Getting core logs from $GIT_REPOS_DIR/core"
cd $GIT_REPOS_DIR/core
git pull --rebase

git log --no-merges --ignore-all-space --pretty="format:%s" --after="$TODAY" >> $TMP_LOGS_DIR/today-commits.txt

git log --no-merges --ignore-all-space --pretty="format:%cd | %s" > $TMP_LOGS_DIR/core-commits.txt
git log -l30000 --no-merges --ignore-all-space --shortstat --ignore-all-space --pretty="format:%cd%n%s" --find-copies > $TMP_LOGS_DIR/core-code-change-commits.txt
git log -l30000 --no-merges --ignore-all-space --pretty="format:%s" --numstat --find-copies > $TMP_LOGS_DIR/core-message-and-changes.txt

echo "Getting aim logs from $GIT_REPOS_DIR/aim"
cd $GIT_REPOS_DIR/aim
git pull --rebase

echo >> $TMP_LOGS_DIR/today-commits.txt
git log --no-merges --ignore-all-space --pretty="format:%s" --after="$TODAY" >> $TMP_LOGS_DIR/today-commits.txt

git log --no-merges --ignore-all-space --pretty="format:%cd | %s" > $TMP_LOGS_DIR/aim-commits.txt
git log -l30000 --no-merges --ignore-all-space --shortstat --ignore-all-space --pretty="format:%cd%n%s" --find-copies > $TMP_LOGS_DIR/aim-code-change-commits.txt

echo "Getting identity logs from $GIT_REPOS_DIR/identity"
cd $GIT_REPOS_DIR/identity
git pull --rebase

echo >> $TMP_LOGS_DIR/today-commits.txt
git log --no-merges --ignore-all-space --pretty="format:%s" --after="$TODAY" >> $TMP_LOGS_DIR/today-commits.txt

echo "Getting track logs from $GIT_REPOS_DIR/track"
cd $GIT_REPOS_DIR/track
git pull --rebase

echo >> $TMP_LOGS_DIR/today-commits.txt
git log --no-merges --ignore-all-space --pretty="format:%s" --after="$TODAY" >> $TMP_LOGS_DIR/today-commits.txt

popd

echo "Getting cruise logs"
curl -s "http://172.18.20.31:8153/go/properties/search?pipelineName=main&stageName=build&jobName=build&limitCount=10000" -o $TMP_LOGS_DIR/build.csv
grep "Failed" $TMP_LOGS_DIR/build.csv | awk -F ',' '{print $8}' > $TMP_LOGS_DIR/failed-builds.txt

echo "Getting completion times for System tests"
curl -s "http://172.18.20.31:8153/go/properties/search?pipelineName=dev-system-tests&stageName=system-tests&jobName=run-system-tests&limitCount=100" | grep Passed | awk -F ',' '{print $6, $13}' > $TMP_LOGS_DIR/system-tests-complete-times

echo "Getting start times for Core"
curl -s "http://172.18.20.31:8153/go/properties/search?pipelineName=core&stageName=build&jobName=build&limitCount=300" | grep Passed | awk -F ',' '{print $6, $8}' | grep "Z$"  > $TMP_LOGS_DIR/core-start-times

echo "Getting start times for Aim"
curl -s "http://172.18.20.31:8153/go/properties/search?pipelineName=aim&stageName=build&jobName=build&limitCount=300" | grep Passed | awk -F ',' '{print $6, $8}'  | grep "Z$"  > $TMP_LOGS_DIR/aim-start-times

echo "Getting start times for Identity"
curl -s "http://172.18.20.31:8153/go/properties/search?pipelineName=identity&stageName=build&jobName=build&limitCount=300" | grep Passed | awk -F ',' '{print $6, $8}'  | grep "Z$"  > $TMP_LOGS_DIR/identity-start-times

echo "Getting start times for Track"
curl -s "http://172.18.20.31:8153/go/properties/search?pipelineName=track&stageName=build&jobName=build&limitCount=300" | grep Passed | awk -F ',' '{print $6, $8}'  | grep "Z$"  > $TMP_LOGS_DIR/track-start-times

rm -rf $GIT_LOGS_DIR
mv $TMP_LOGS_DIR $GIT_LOGS_DIR
