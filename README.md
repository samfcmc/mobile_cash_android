mobile_cash_android
===================

Prerequisites:
- Android SDK;
- Android SDK Platform API 19
- Maven
- Eclipse
- Android x86 Virtual Machine (optional)

Clone the repo

Compile project with maven
<code> mvn install </code>

Deploy application in Android device (Virtual device or a real one connected through USB port)
<code> mvn android:deploy </code>

Run the deployed application
<code> mvn android:run </code>

Using Android x86
- You can run the project in an Android x86 virtual machine
	1- Check ip address of the virtual machine
	2- Do
<code> adb connect ip_of_Android_x86 </code>
	3- Now you can deploy and run the application as described above


In case of error when building the project with Maven, if you receive the MojoException : Could not find the path
to Android SDK then:

- Create a file settings.xml in ${HOME}/.m2 directory 
- Add this code into the file :
<myxml>
  <settings> 
     <profiles>
     	 <profile>
           <id>android-settings</id>
           <praoperties>
            <android.sdk.path>/path/to/android/sdk</android.sdk.path>
        </properties>
      </profile>
     </profiles>
     <activeProfiles>
        <activeProfile>android-settings</activeProfile>
     </activeProfiles>
  </settings>
</myxml> 

Have fun ;)
