pushd .

cd /Users/rudayaku/Projects/core
git checkout master
git pull --rebase
git log --no-merges --pretty="format:%cd | %s" > /Users/rudayaku/Projects/clojure-samples/GitStats/core-commits.txt
git log -l30000 --no-merges --shortstat --ignore-all-space --pretty="format:%cd%n%s" --find-copies > /Users/rudayaku/Projects/clojure-samples/GitStats/core-code-change-commits.txt
git log -l30000 --no-merges --pretty="format:%s" --numstat --find-copies > /Users/rudayaku/Projects/clojure-samples/GitStats/core-message-and-changes.txt

cd /Users/rudayaku/Projects/casper-aim
git checkout springer
git pull --rebase
git log --no-merges --pretty="format:%cd | %s" > /Users/rudayaku/Projects/clojure-samples/GitStats/aim-commits.txt

popd

curl "http://172.18.20.31:8153/go/properties/search?pipelineName=main&stageName=build&jobName=build&limitCount=10000" -o build.csv
grep "Failed" build.csv | awk -F ',' '{print $8}' > failed-builds.txt