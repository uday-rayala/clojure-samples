export GIT_LOGS_DIR=/home/casper/graphs/clojure-samples/GitStats/logs
export GIT_REPOS_DIR=/home/casper/graphs/repos
export TMP_LOGS_DIR=/home/casper/graphs/tmplogs


while [ true ]
do
  ./generate.sh
  sleep 120
done