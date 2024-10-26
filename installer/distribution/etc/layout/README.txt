Meldware Communication Suite Installation and Instructions

=== Welcome ===

Congratulations, you've successfully downloaded and begun the graphical install process for Meldware 
Communication Suite.  This installer allows you to install a completely new instance of JBoss Application 
Server, which this version of Meldware Communication Suite depends on.

The configuration of JBoss Application Server that is distributed with this bundle is a default deployment.  
After installation, you should take some effort to secure the application server per your organization's 
security policies and infrastructrue, a basic guide to help you begin is here: 
http://wiki.jboss.org/wiki/Wiki.jsp?page=SecureJBoss.  Moreover many services are included in this release 
that are not strictly required for the mail server.  For information on thinning JBoss see this guide: 
http://wiki.jboss.org/wiki/Wiki.jsp?page=JBossASTuningSliming.

=== Installing with a NEW JBoss Application Server ===

This release includes JBossAS 4.0.4GA.  Merely select the installation directory, accept the 
JBoss Application Server pack and continue.   

=== Everyone ===

The installation process directs your through a basic Meldware Communication Suite configuration.  It 
does not, however, allow you to configure every permutation of configuration that MCS supports.  It is 
suggested that you look here: 
http://www.buni.org/mediawiki/index.php/Meldware_Installation_Guide_v_1.0M8#Configuring_Meldware
after the installation for further configuration advice.  Moreover we are actively looking for feedback via 
the Meldware Communication Suite user forums: http://buni.org/forums/ as well as the developmnet mail 
lists: http://www.buni.org/mediawiki/index.php/Mail_Lists and welcome your participation.

This is a milestone release and while the authors have made great strides in producing a stable and 
robust mail and calendar server, and there are organizations serving as early adopters, we recommend 
rigorous testing before placing it in production.  It is suggested that POP and SMTP are rated at 
production quality and have been used as such while IMAP and Webmail are still alpha or beta quality and 
should probably only be deployed in test and development for pre-implementation.

The installation process allows you to create a keystore, this is however a "self signed" key.  This means 
that mail clients may refuse the key or at least warn users that the key is not verified by a trusted 
certificate authority.  It is suggested that you acquire a certificate from Thawte or Verisign or a CA 
trusted by your perferred mail client.

The installation process also allows you to either accept a "default" datasource using the Hypersonic 
database or create a datasource.  It is STRONGLY suggested that you create a datasource rather than use 
hypersonic.  Hypersonic is only approrpiate for small mail servers and basic configuration testing.  While 
the installer can create configurations for Oracle, PostgreSQL, MySQL and Hypersonic, other databases with 
JDBC drivers should work, but are untested.  The installer can install your JDBC driver for you (postgresql
or MySQL).  However, if your driver is not included, then you must must copy that to the 
$INSTALL/server/default/lib directory (except for Hypersonic which is included with JBAS).  It is suggested 
that you download the JDBC driver from your vendor and place it in the above mentioned directory.

The installation process will configure a database user repository which can be edited with the Administration
Tool (http://www.buni.org/mediawiki/index.php/Meldware_Installation_Guide_v_1.0M8#Configuring_Meldware).  However, MCS 
also supports other JBAS JAAS user repositories.  An example configruation is included in the 
server/default/deploy/mail.ear/mail.sar/META-INF directory.  See the 
http://www.buni.org/mediawiki/index.php/Meldware_Installation_Guide_v_1.0M8#JAAS wiki page for more 
details.  We encourage you to use LDAP or the default database driven security through JAAS for this release.

== JMS ==

MCS uses Java Messaging Service.  The default JMS configuration with JBoss App Server uses Hypersonic.  
The installer does not reconfigure JMS to use a different database.  You may want to do this as 
Hypersonic will load the body of the JMS message (does not include the message body so no 
worries there) in to memory.  

http://www.buni.org/mediawiki/index.php/Meldware_Installation_Guide_v_1.0M8#Manually_Configuring_Datasources
http://www.buni.org/mediawiki/index.php/Meldware_Installation_Guide_v_1.0M8#Configuring_JMS_Persistence

== APOP ==

This release of MCS includes support for "APOP" (http://en.wikipedia.org/wiki/APOP).  Unlike previous versions
the graphical installer configures it by default.

=== Don't Panic ===

The length of this readme makes installation sound much harder than it is.  Just go through the step by step 
GUI, you can always go "back" and read this if you have questions.  

=== Future ===

Presently configuration is kind of XML-centric.  Its not as bad as most other mail servers but is not 
presently as easy as a graphical interface.  We are planning a GUI configuration system for the 1.0 final 
release.  

=== Feedback ===

We really want your feedback on MCS and the installation.  Please join us in the Buni.org forums:  
http://buni.org/forums/ and feel free to ask questions.

Thanks,

The Developers of Meldware Communication Suite
Buni.org

