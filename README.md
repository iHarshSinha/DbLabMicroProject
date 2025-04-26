

commands to run

export CLASSPATH="bin:lib/mysql-connector-j-8.0.32.jar:lib/javax.mail.jar:lib/activation-1.1.1.jar"
javac -d bin $(find src -name "*.java")
java com.BanjaraHotels.App