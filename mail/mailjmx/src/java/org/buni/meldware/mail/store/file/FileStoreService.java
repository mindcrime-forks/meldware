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
package org.buni.meldware.mail.store.file;

import org.buni.meldware.mail.store.StoreMBean;

/**
 * The File Store Service is an alternative to the database store services. It
 * uses the FileStore API to store mail parts on the filesystem. IDs are
 * assigned sequentially (order not guaranteed but duplicates prevented). IDs
 * are parsed into directories and files. The ones position is the only concrete
 * file and is prepended with the letter X. Thus an ID of 100 would be
 * $PATH/1/0/X0
 * 
 * @author Andrew C. Oliver
 * 
 */
public interface FileStoreService extends StoreMBean {

    /**
     * set the path for the base of the filestore
     * 
     * @param path
     */
    void setPath(String path);

    /**
     * @return path at the root of the filestore.
     */
    String getPath();

    /**
     * Generally this does nothing. Sometimes due to varies file locking
     * parameters we can't get an ID straight away. This retries the inital
     * allocation. It has NO effect on the actual writing of the file.
     * 
     * @param tries
     */
    void setAttemptsPerAllocate(int tries);

    /**
     * @return attempts to allocate a file (see setter)
     */
    int getAttemptsPerAllocate();
}