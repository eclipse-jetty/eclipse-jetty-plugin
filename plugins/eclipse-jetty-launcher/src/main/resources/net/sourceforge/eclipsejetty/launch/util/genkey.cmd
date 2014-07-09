
"%JAVA_HOME%\bin\keytool" -genkey -v -keyalg RSA -alias eclipsejettyplugin -keypass "correct horse battery staple" -storepass "correct horse battery staple" -dname "CN=localhost, OU=-, O=Eclipse Jetty Plugin, L=-, S=-, C=AT" -validity 36500 -keystore eclipseJettyPlugin.keystore

"%JAVA_HOME%\bin\keytool" -exportcert -alias eclipsejettyplugin -storepass "correct horse battery staple" -keystore eclipseJettyPlugin.keystore -file eclipsejettyplugin.cer
