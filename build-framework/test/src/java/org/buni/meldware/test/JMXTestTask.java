package org.buni.meldware.test;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.jboss.jmx.adaptor.rmi.RMIAdaptor;

/**
 * Ant task for running JUnit tests via JMX.  Will
 * transalate fileSets into classes that will be used
 * as number test suites.
 * 
 * @author Michael Barker
 *
 */
public class JMXTestTask extends Task
{
   private final static String CLASS_SUFFIX = ".class";
   private final Collection<FileSet> fileSets;
   private String url = "jnp://localhost:1099";
   private String invoker = "jmx/rmi/RMIAdaptor";
   private String mbeanName = "meldware.mail:type=MailServices,name=TestRunner";
   private boolean useXml = false;
   private File outputDir = null;
   
   public JMXTestTask()
   {
      fileSets = new ArrayList<FileSet>();
   }
   
   /**
    * Connect to the JMXTestRunner and run all of the
    * test suites specified in the filesets.
    * 
    * @see org.apache.tools.ant.Task#execute()
    */
   public void execute() throws BuildException
   {
      Set<String> testSuites = new LinkedHashSet<String>();
      
      for (FileSet fs : fileSets)
      {
         String[] classFiles = fs.getDirectoryScanner(getProject()).getIncludedFiles();
         for (int i = 0; i < classFiles.length; i++)
         {
            String classFile = classFiles[i];
            if (classFile.endsWith(CLASS_SUFFIX))
            {
               classFile = classFile.substring(0, classFile.length() - CLASS_SUFFIX.length());
               classFile = classFile.replaceAll("/|\\\\", ".");
               testSuites.add(classFile);
            }
         }
      }
      
      Hashtable<String,String> h = new Hashtable<String,String>();
      h.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
      h.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
      h.put("java.naming.provider.url", url);
      
      try
      {
         InitialContext ic = new InitialContext(h);
         MBeanServerConnection server = (RMIAdaptor) ic.lookup(invoker);
         JMXTestRunnerMBean testRunner = (JMXTestRunnerMBean) 
         	MBeanServerInvocationHandler.newProxyInstance
         		(server, new ObjectName(mbeanName), JMXTestRunnerMBean.class, false);
         
         for (String testSuite : testSuites)
         {
            String result;
            System.out.print("Running: " + testSuite + "... ");
            if (useXml)
            {
               result = testRunner.runTestXML(testSuite);               
            }
            else
            {
               result = testRunner.runTest(testSuite);
            }
            System.out.println("done.");
            if (outputDir != null)
            {
               String filename = "TEST-" + testSuite + ".xml";
               File outputFile = new File(outputDir, filename);
               Writer out = new BufferedWriter(new FileWriter(outputFile));
               out.write(result);
               out.flush();
               out.close();
            }
         }
      }
      catch (NamingException e)
      {
         throw new BuildException(e);
      }
      catch (MalformedObjectNameException e)
      {
         throw new BuildException(e);
      }
      catch (IOException e)
      {
         throw new BuildException(e);
      }
   }
   
   public void addFileSet(FileSet fs)
   {
      fileSets.add(fs);
   }
   
   public void setUrl(String url)
   {
      this.url = url;
   }
   
   public void setUseXml(boolean b)
   {
      useXml = b;
   }
   
   public void setOutputDir(File dir)
   {
      outputDir = dir;
   }
}
