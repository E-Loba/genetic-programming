(defproject test-test "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.trace "0.7.5"]]
  :jvm-opts ["-Xmx1g" "-XX:-OmitStackTraceInFastThrow"] 
  :java-source-paths ["src/test_test"])
