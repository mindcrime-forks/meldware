/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2007, Bunisoft LLC.,
 *
 * Portions of this software are Copyright 2006, JBoss Inc., and 
 * individual contributors as indicated by the @authors tag.
 * See the copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.buni.izpack;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import bsh.Interpreter;

import com.izforge.izpack.Pack;
import com.izforge.izpack.PackFile;
import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.util.AbstractUIProgressHandler;
import com.izforge.izpack.util.Debug;
import com.izforge.izpack.util.VariableSubstitutor;

public class InstallerListener implements
		com.izforge.izpack.event.InstallerListener {

	private AutomatedInstallData installData = null;
    private final static String VM_SUFFIX = ".vm";

	private VariableSubstitutor varResolver = null;

	private Interpreter interpreter = null;

	private Map<String,Collection<String[]>> taskMap = new HashMap<String,Collection<String[]>>();
    private List<File> toDelete = new ArrayList<File>();

	public void afterDir(File arg0, PackFile arg1) throws Exception {
		// TODO Auto-generated method stub

	}

	public void afterFile(File file, PackFile pack) throws Exception {
		if (pack.getAdditionals().containsKey("templatefilter")) {
			String filter = (String) pack.getAdditionals()
					.get("templatefilter");
			this.interpreter.set("targetfile", file);
			this.interpreter.set("packfile", pack);
			Object result = this.interpreter.eval(filter);
			Debug.trace("Evaluated filer: " + filter + "value: " + result);
			if (Boolean.class.isInstance(result)) {
				// if have to filter out the file
				if (((Boolean) result).booleanValue()) {
					file.delete();
					return;
				}
			} else {
				Debug.error("filter: " + filter
						+ " did not return a Boolean");
			}
		}
		if (pack.getAdditionals().containsKey("template")) {
			String templateRoot = (String) pack.getAdditionals()
					.get("template");
			templateRoot = this.varResolver.substitute(templateRoot,
					VariableSubstitutor.PLAIN);
            File templateRootDir = new File(templateRoot);
			if (file.getAbsolutePath().startsWith(templateRootDir.getAbsolutePath())) {

				String templatePath = file.getAbsolutePath().substring(
						templateRoot.length() + 1);

				String targetPath = file.getAbsolutePath();
                if (targetPath.endsWith(VM_SUFFIX)) {
                   targetPath = targetPath.substring(0, targetPath.length() - VM_SUFFIX.length()); 
                    // task consist of template,targetpath,oldfiletobedeleted
                    String[] task = new String[] { templatePath, targetPath,
                            file.getAbsolutePath() };
                    this.addTemplateTask(templateRoot, task);
                    Debug.trace("Adding template: " + file);
                } else {
                    toDelete.add(file);
                    Debug.trace("Adding data file: " + file);
                }
                
			} else {
				Debug.error("Skipping template: " + file.getAbsolutePath()
						+ " because it is not in direcotry: " + templateRoot);
			}
		}
	}

	public void afterPack(Pack pack, Integer arg1,
			AbstractUIProgressHandler arg2) throws Exception {
		Debug.trace(" ** After packs ** ");
	}

	public void afterPacks(AutomatedInstallData installData,
			AbstractUIProgressHandler uiHandler) throws Exception {
		processTemplateRoots();
	}

	public void beforeDir(File arg0, PackFile arg1) throws Exception {
		// TODO Auto-generated method stub

	}

	public void beforeFile(File arg0, PackFile arg1) throws Exception {
		// TODO Auto-generated method stub

	}

	public void beforePack(Pack arg0, Integer arg1,
			AbstractUIProgressHandler arg2) throws Exception {
		// TODO Auto-generated method stub

	}

	public void beforePacks(AutomatedInstallData installData, Integer id,
			AbstractUIProgressHandler handler) throws Exception {
		this.installData = installData;
		this.varResolver = new VariableSubstitutor(installData.getVariables());
		this.interpreter = new Interpreter();
		this.interpreter.set("config", installData.getVariables());
	}

	public boolean isFileListener() {
		return true;
	}

	public void addTemplateTask(String templateRoot, String[] task) {
		Collection<String[]> tasks = taskMap.get(templateRoot);

		if (tasks == null) {
			tasks = new Vector<String[]>();
			taskMap.put(templateRoot, tasks);
		}

		Debug.trace(" Template Root: " + templateRoot + " template: " + task[0]);
        
		tasks.add(task);
	}

	public void processTemplateRoots() {
		Debug.trace(" ** Process Templates ** ");
		VelocityEngine ve;
		try {
			ve = configureVelocity();
		} catch (Exception e) {
			Debug.error(e);
			return;
		}

		Iterator templateRoots = this.taskMap.keySet().iterator();
		while (templateRoots.hasNext()) {
			String root = (String) templateRoots.next();
			Collection tasks = (Collection) this.taskMap.get(root);
			processTemplate(root, tasks, ve);
		}
        for (File f : toDelete) {
            f.delete();
        }

	}

	public void processTemplate(String root, Collection tasks, VelocityEngine ve) {
		Debug.trace("Process Template Root:" + root);
		Iterator templates = tasks.iterator();
		while (templates.hasNext()) {
			String[] task = (String[]) templates.next();
			String template = task[0];
			String output = task[1];
			String delete = task[2];
            String dataSourceName;
			VelocityContext vc = new VelocityContext(installData.getVariables());
            // TODO: Make this a bit better (have a mapping table)
            if ("hsqldb".equals(installData.getVariable("DATABASE_TYPE"))) {
                dataSourceName = "DefaultDS";
            } else {
                dataSourceName = "MeldwareDS";
            }
            installData.setVariable("DATASOURCE_NAME", dataSourceName);
            installData.setVariable("JAAS_DATASOURCE_NAME", dataSourceName);
			vc.put("template-error", "oops!!");
			try {
                Debug.trace("Starting Template:" + template);
				BufferedWriter out = new BufferedWriter(new FileWriter(output));
				boolean status = ve.mergeTemplate(template, vc, out);
				out.close();
				File todelete = new File(delete);
				Debug.trace("Processed Template:" + template + " status:"
						+ status + " deleted: " + todelete.delete());
			} catch (Exception e) {
				Debug.trace(e);
			}
		}
	}

	public VelocityEngine configureVelocity() throws Exception {
		Iterator templateRoots = this.taskMap.keySet().iterator();
		String root = null;

		// Init velocity engine
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty("runtime.log.logsystem.class",
				"org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
		ve.setProperty("runtime.log.logsystem.log4j.category",
				"testSecureJmxConsole.VelocityEngine");
		ve.setProperty("resource.loader", "file");
		ve.setProperty("file.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
		ve.setProperty("file.resource.loader.cache", "false");
		ve.setProperty("file.resource.loader.modificationCheckInterval", "0");

		while (templateRoots.hasNext()) {
			if (root == null)
				root = (String) templateRoots.next();
			else
				root += ", " + (String) templateRoots.next();
		}
		ve.setProperty("file.resource.loader.path", root);

		ve.init();
		Debug.trace("Initialized VelocityEngine");
		return ve;
	}
}
