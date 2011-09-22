pushd .

LOGS_DIR=/Users/rudayaku/Projects/clojure-samples/GitStats/logs
TMP_LOGS_DIR=/tmp/logs
REPOS_DIR=/Users/rudayaku/repos
TODAY=`date "+%b %d 00:00:00 %Y"`

rm -rf $TMP_LOGS_DIR
mkdir $TMP_LOGS_DIR

echo "Getting core logs from $REPOS_DIR/core"
cd $REPOS_DIR/core
git pull --rebase

git log --no-merges --ignore-all-space --pretty="format:%s" --after="$TODAY" >> $TMP_LOGS_DIR/today-commits.txt
git log --no-merges --ignore-all-space --pretty="format:%cd | %s" > $TMP_LOGS_DIR/core-commits.txt
git log -l30000 --no-merges --ignore-all-space --shortstat --ignore-all-space --pretty="format:%cd%n%s" --find-copies > $TMP_LOGS_DIR/core-code-change-commits.txt
git log -l30000 --no-merges --ignore-all-space --pretty="format:%s" --numstat --find-copies > $TMP_LOGS_DIR/core-message-and-changes.txt

echo "Getting core logs from $REPOS_DIR/aim"
cd $REPOS_DIR/aim
git pull --rebase

echo >> $TMP_LOGS_DIR/today-commits.txt
git log --no-merges --ignore-all-space --pretty="format:%s" --after="$TODAY" >> $TMP_LOGS_DIR/today-commits.txt
git log --no-merges --ignore-all-space --pretty="format:%cd | %s" > $TMP_LOGS_DIR/aim-commits.txt
git log -l30000 --no-merges --ignore-all-space --shortstat --ignore-all-space --pretty="format:%cd%n%s" --find-copies > $TMP_LOGS_DIR/aim-code-change-commits.txt


popd

echo "Getting cruise logs"
curl "http://172.18.20.31:8153/go/properties/search?pipelineName=main&stageName=build&jobName=build&limitCount=10000" -o $TMP_LOGS_DIR/build.csv
grep "Failed" $TMP_LOGS_DIR/build.csv | awk -F ',' '{print $8}' > $TMP_LOGS_DIR/failed-builds.txt

rm -rf $LOGS_DIR
cp -R $TMP_LOGS_DIR $LOGS_DIR
