/*
 * Bunisoft the Open Source Communications Company
 * Copyright 2006, Bunisoft Inc., and individual contributors as
 * indicated by the @authors tag.  See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; version 2.1 of
 * the License.
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
package org.buni.meldware.mail.store.s3;

import org.buni.meldware.mail.store.StoreMBean;

/**
 * The S3 File Store Service is an alternative to the database store services,
 * and it similar to the Meldware File Store Service, except that this service 
 * persists all of its data to the Amazon S3 online storage service (http//www.amazon.com/aws).
 * This service uses the S3FileStore API to store mail parts on the S3 filesystem. 
 * IDs are
 * assigned sequentially (order not guaranteed but duplicates prevented). IDs
 * are parsed into directories and files. The ones position is the only concrete
 * file and is prepended with the letter X. Thus an ID of 100 would be
 * $PATH/1/0/X0
 * 
 * @author Andrew C. Oliver
 * @author David Fuelling (sappenin@gmail.com)
 * 
 */
public interface S3FileStoreService extends StoreMBean {

	//Getters and Setters.
	
	/**
	 * @return Returns the awsSystemAccessKey.
	 */
	public String getAwsSystemAccessKey();
	/**
	 * @return Returns the awsSystemSecretAccessKey.
	 */
	public String getAwsSystemSecretAccessKey();


	/**
	 * @return Returns the defaultBucket.
	 */
	public String getDefaultBucket();
	/**
	 * @return Returns the defaultPath.
	 */
	public String getDefaultPath();
	/**
	 * @return Returns the defaultS3Host.
	 */
	public String getDefaultS3Host();

	/**
	 * @return Returns the maxS3FileSize.
	 */
	public long getMaxS3FileSize();
	/**
	 * @return Returns the numRetries.
	 */
	public int getNumRetries();
	
	public int getRetrySleep();
	
	/**
	 * @return Returns the useSSL.
	 */
	public boolean isUseSSL();
	
	/**
	 * @param awsSystemAccessKey The awsSystemAccessKey to set.
	 */
	public void setAwsSystemAccessKey(String awsSystemAccessKey);

	/**
	 * @param awsSystemSecretAccessKey The awsSystemSecretAccessKey to set.
	 */
	public void setAwsSystemSecretAccessKey(String awsSystemSecretAccessKey);
	/**
	 * @param defaultBucket The defaultBucket to set.
	 */
	public void setDefaultBucket(String defaultBucket);

	/**
	 * @param defaultPath The defaultPath to set.
	 */
	public void setDefaultPath(String defaultPath);
	/**
	 * @param defaultS3Host The defaultS3Host to set.
	 */
	public void setDefaultS3Host(String defaultS3Host);
	/**
	 * @param maxS3FileSize The maxS3FileSize to set.
	 */
	public void setMaxS3FileSize(long maxS3FileSize);
	/**
	 * @param numRetries The numRetries to set.
	 */
	public void setNumRetries(int numRetries);
	
	public void setRetrySleep(int retrySleep);
	
	/**
	 * @param useSSL The useSSL to set.
	 */
	public void setUseSSL(boolean useSSL);
}