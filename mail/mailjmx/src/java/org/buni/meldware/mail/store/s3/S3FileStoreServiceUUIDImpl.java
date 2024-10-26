/*
 * Bunisoft the Open Source Communications Company Copyright 2006, Bunisoft
 * Inc., and individual contributors as indicated by the @authors tag. See the
 * copyright.txt in the distribution for a full listing of individual
 * contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; version 2.1 of the License.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.buni.meldware.mail.store.s3;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.buni.meldware.mail.store.StoreException;
import org.buni.meldware.mail.store.StoreItemMetaData;
import org.buni.s3filestore.S3FileStore;
import org.jboss.aspects.tx.Tx;
import org.jboss.aspects.tx.TxType;

/**
 * Uses our new handy dandy filestore stores body parts in a series of
 * subdirectories on the Amazon AWS S3 FileSystem..
 * 
 * @author Andrew C. Oliver
 * @author David Fuelling
 * 
 */

/**
 * THIS CLASS IS NOT READY FOR PRIMETIME SINCE IT BREAKS THE ABSTRACTSTORE
 * INTERFACE.
 */
public class S3FileStoreServiceUUIDImpl //extends AbstractStore implements S3FileStoreService
{
	S3FileStore s3FileStore;
	
	/**
	 * AWS Store Properties
	 */
	private String defaultS3Host = "s3.amazonws.com";
	private boolean useSSL = new Boolean(true);
	private String awsSystemAccessKey;
	private String awsSystemSecretAccessKey;
	private String defaultBucket = "";
	private String defaultPath = "meldware/messages";
	
	private int numRetries = 3;
	private int retrySleep = 2000;
	
	//Rarely Changes
	private long maxS3FileSize = 5368709120L;
	
	public void init()
	{
		Properties props = new Properties();
		props.put("defaultS3Host", defaultS3Host);
		props.put("useSSL", useSSL);
		props.put("awsSystemAccessKey", awsSystemAccessKey);
		props.put("awsSystemSecretAccessKey", awsSystemSecretAccessKey);
		props.put("defaultBucket", defaultBucket);
		props.put("defaultPath", defaultPath);
		props.put("maxS3FileSize", maxS3FileSize);
		props.put("numRetries", numRetries);
		props.put("retrySleep", retrySleep);
		
		
		this.s3FileStore = new S3FileStore(props);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.mail.store.AbstractStore#doCreate()
	 */
	//@Override
	protected UUID doCreate() throws StoreException
	{
		try
		{
			/*
             * S3 has a last-write 'wins' policy, such that the last file to be written with a given file name
             * in a given bucket is the version of the file that is written.  Thus, we need to ensure that a given
             * file has a unique bucketname+key in the AWS system.  We use UUID to do this.
			 */ 
			/*Properties properties = new Properties();
			//properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
			//properties.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
			//properties.put(Context.PROVIDER_URL,"jnp://" + this.getJNPProviderURL() + ":1099");
			InitialContext ctx = new InitialContext(properties);
			String sJNDIName = "mail/" + S3StoreIdGeneratorImpl.class.getSimpleName() + "/local";
			S3StoreIdGenerator keygen = (S3StoreIdGenerator)  ctx.lookup(sJNDIName);
			return keygen.getNextId(); */
            
            return UUID.randomUUID();
		}
		catch (Exception e)
		{
			throw new StoreException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.mail.store.StoreMBean#cleanUp(java.util.List)
	 */
	public void cleanUp(List<UUID> bods)
	{
		for (int i = 0; i < bods.size(); i++)
		{
			this.delete(bods.get(i));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.mail.store.StoreMBean#delete(java.util.UUID)
	 */
	public void delete(UUID id) throws StoreException
	{
		s3FileStore.delete(id);
	}

	/**
	 * 
	 * @see org.buni.meldware.mail.store.Store#getBuffer(java.lang.Object, long,
	 *      int)
	 * 
	 */
	@Tx(TxType.REQUIRED)
	public ByteBuffer getBuffer(UUID id, long position, int len) throws StoreException
	{
		ByteBuffer buffer;
		try
		{
			InputStream iStream = s3FileStore.getInputStream(id, position, len);
			byte[] b = new byte[len];
            int numRead = iStream.read(b, 0, len);
            buffer = ByteBuffer.wrap(b, 0, numRead);
            iStream.close();
        } catch (Exception e) {
            throw new StoreException(e);
        }

        return buffer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.mail.store.StoreMBean#getInputStream(java.util.UUID,
	 *      org.buni.meldware.mail.store.StoreItemMetaData)
	 */
	public InputStream getInputStream(UUID id, StoreItemMetaData meta) throws StoreException
	{
		try
		{
			return s3FileStore.getInputStream(id, null);
		}
		catch (Exception e)
		{
			throw new StoreException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.mail.store.StoreMBean#getOutputStream(java.util.UUID,
	 *      org.buni.meldware.mail.store.StoreItemMetaData)
	 */
	public OutputStream getOutputStream(UUID id, StoreItemMetaData meta) throws StoreException
	{
		try
		{
			return s3FileStore.getOutputStream(id, null);
		}
		catch (Exception e)
		{
			throw new StoreException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.mail.store.StoreMBean#idToString(java.lang.Object)
	 */
	public String idToString(Object id)
	{
		return id.toString();
	}

	/**
	 * @param id The id of the store item to read.
     * @param position The position in the stream to begin reading from 
     * (The offset in the store to start reading at).
     * @param b The byte buffer to read into (write to)
	 * @param off The offset in the byte buffer to write into
	 * @param len The number of bytes to write.
	 * @return The Number of bytes written.
	 * 
	 * @see org.buni.meldware.mail.store.StoreMBean#read(java.util.UUID, long,
	 *      byte[], int, int)
	 */
	public int read(UUID id, long position, byte[] b, int off, int len) throws StoreException
	{
		int read = 0;
		try
		{
			//Position the stream on S3 appropriately by starting it at (position).
			InputStream iStream = s3FileStore.getInputStream(id, position, -1, null);
			//stream.skip(position);
			read = iStream.read(b, off, len);
			iStream.close();
		}
		catch (Exception e)
		{
			throw new StoreException(e);
		}
		return read;
	}

	/**
	 * @param id The id of the store item to wrtte to.
     * @param position The position in the stream to begin writing to.
     * @param b The byte buffer to read from.
	 * @param off The offset in the byte buffer to read from
	 * @param len The number of bytes to write.
	 * @return The Number of bytes written.
	 * 
	 * @see org.buni.meldware.mail.store.StoreMBean#write(java.util.UUID, long,
	 *      byte[], int, int)
	 */
	public Object stringToUUID(String s)
	{
		return UUID.fromString(s);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.buni.meldware.mail.store.StoreMBean#write(java.util.UUID, long,
	 *      byte[], int, int)
	 */
	public int write(UUID id, long position, byte[] b, int off, int len) throws StoreException
	{
		throw new StoreException("Amazon S3 does not allow partial writes.  Please use 'getOutputStream(..) instead to write an entire file.");
	}
	
	
	//Getters and Setters.

	/**
	 * @return Returns the awsSystemAccessKey.
	 */
	public String getAwsSystemAccessKey()
	{
		return awsSystemAccessKey;
	}

	/**
	 * @param awsSystemAccessKey The awsSystemAccessKey to set.
	 */
	public void setAwsSystemAccessKey(String awsSystemAccessKey)
	{
		this.awsSystemAccessKey = awsSystemAccessKey;
	}

	/**
	 * @return Returns the awsSystemSecretAccessKey.
	 */
	public String getAwsSystemSecretAccessKey()
	{
		return awsSystemSecretAccessKey;
	}

	/**
	 * @param awsSystemSecretAccessKey The awsSystemSecretAccessKey to set.
	 */
	public void setAwsSystemSecretAccessKey(String awsSystemSecretAccessKey)
	{
		this.awsSystemSecretAccessKey = awsSystemSecretAccessKey;
	}

	/**
	 * @return Returns the defaultBucket.
	 */
	public String getDefaultBucket()
	{
		return defaultBucket;
	}

	/**
	 * @param defaultBucket The defaultBucket to set.
	 */
	public void setDefaultBucket(String defaultBucket)
	{
		this.defaultBucket = defaultBucket;
	}

	/**
	 * @return Returns the defaultPath.
	 */
	public String getDefaultPath()
	{
		return defaultPath;
	}

	/**
	 * @param defaultPath The defaultPath to set.
	 */
	public void setDefaultPath(String defaultPath)
	{
		this.defaultPath = defaultPath;
	}

	/**
	 * @return Returns the defaultS3Host.
	 */
	public String getDefaultS3Host()
	{
		return defaultS3Host;
	}

	/**
	 * @param defaultS3Host The defaultS3Host to set.
	 */
	public void setDefaultS3Host(String defaultS3Host)
	{
		this.defaultS3Host = defaultS3Host;
	}

	/**
	 * @return Returns the maxS3FileSize.
	 */
	public long getMaxS3FileSize()
	{
		return maxS3FileSize;
	}

	/**
	 * @param maxS3FileSize The maxS3FileSize to set.
	 */
	public void setMaxS3FileSize(long maxS3FileSize)
	{
		this.maxS3FileSize = maxS3FileSize;
	}

	/**
	 * @return Returns the numRetries.
	 */
	public int getNumRetries()
	{
		return numRetries;
	}

	/**
	 * @param numRetries The numRetries to set.
	 */
	public void setNumRetries(int numRetries)
	{
		this.numRetries = numRetries;
	}

	/**
	 * @return Returns the retrySleep.
	 */
	public int getRetrySleep()
	{
		return retrySleep;
	}

	/**
	 * @param retrySleep The retrySleep to set.
	 */
	public void setRetrySleep(int retrySleep)
	{
		this.retrySleep = retrySleep;
	}

	/**
	 * @return Returns the useSSL.
	 */
	public boolean isUseSSL()
	{
		return useSSL;
	}

	/**
	 * @param useSSL The useSSL to set.
	 */
	public void setUseSSL(boolean useSSL)
	{
		this.useSSL = useSSL;
	}
}
