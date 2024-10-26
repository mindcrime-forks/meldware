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
package org.buni.meldware.calendar.session;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;

import org.buni.meldware.calendar.data.Address;
import org.buni.meldware.calendar.data.ContactSortKeys;
import org.buni.meldware.calendar.data.CursorDirection;
import org.buni.meldware.calendar.eventbus.AddressChange;
import org.buni.meldware.calendar.session.exception.DuplicateUserAddressException;
import org.buni.meldware.calendar.session.exception.RecordNotFoundException;
import org.buni.meldware.calendar.session.exception.UserAddressUnknownException;
import org.buni.meldware.calendar.session.exception.UserNotAuthorizedException;
import org.buni.meldware.calendar.session.exception.UserUnknownException;
import org.buni.meldware.calendar.util.HibernateLookUp;
import org.buni.meldware.common.preferences.UserProfile;
import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 * Address Manager is to handle all business rules around Address entities.
 * 
 * @author $Author: andy $
 * @version $Revision: 1.4 $
 * 
 * @ejb.bean name="AddressManager" description="Service to manage address
 *           records" jndi-name="ejb/session/AddressManager" type="Stateless"
 * @ejb.transaction type="Required"
 * @ejb.transaction-type type="Container"
 * @ejb.util generate="physical"
 * @ejb.permission unchecked="true"
 * 
 * @ejb.env-entry name="profileServiceName" type="java.lang.String" value="meldware.base:type=UserProfileService,name=UserProfileService"
 */
public class AddressManagerBean extends Service implements SessionBean {
	static final long serialVersionUID = "$Id: AddressManagerBean.java,v 1.4 2007/12/30 01:58:40 andy Exp $".hashCode(); //$NON-NLS-1$

	/**
	 * Creates a new AddressManagerBean object.
	 */
	public AddressManagerBean() {
	}

	/**
	 * Create new addresses
	 * 
	 * @param address
	 *            DOCUMENT ME!
	 * @param ownerName
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="local"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
     * @ejb.
	 */
	public String[] createAddresses(String[] address, String ownerName)
			throws EJBException, UserUnknownException {
		Session session = null;
		Date createDate = new Date();
		this.log.debug("start create addresses");

		try {
			session = HibernateLookUp.getSessionFactory().openSession();

			UserProfile owner = getUser(ownerName, session);
            owner.getAliases().addAll(Arrays.asList(address));
            profileService.updateUserProfile(owner);
			String[] addresses = owner.getAliases().toArray(new String[]{});
			
			return addresses;
		} catch (HibernateException e) {
			throw new EJBException(e);
		} finally {
			try {
				if (session != null) {
					session.close();
				}
			} catch (HibernateException e) {
				this.log.error("Failed to close Hibernate Session", e);
			}
		}
	}

	/**
	 * Share existing addresses with current user.
	 * 
	 * @param userNames
	 *            DOCUMENT ME!
	 * @param ownerName
	 *            DOCUMENT ME!
	 * 
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="local"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public void addFriends(String[] userNames, String ownerName)
			throws UserUnknownException {
	//	Session session = null;
		this.log.debug("start share addresses");
//TODO ACO NOOP
	//	try {
            /*
			session = HibernateLookUp.getSessionFactory().openSession();

			User owner = getUser(ownerName, session);

			for (int i = 0; i < userNames.length; i++) {
				try {
					Address friend = getUserAddress(userNames[i]);
					owner.getAddresses().add(friend);
					this.log.debug("shared address: " + friend.getRecordId());
					publishEvent(new AddressChange(this.sessionContext
							.getCallerPrincipal().getName(), friend,
							AddressChange.ADDRESS_SHARED));
				} catch (UserAddressUnknownException e1) {
					this.log.warn("Possible error user:" + userNames[i]
							+ " has no registered address.");
				}
			}

			session.flush();
		} catch (HibernateException e) {
			throw new EJBException(e);
		} finally {
			try {
				if (session != null) {
					session.close();
				}
			} catch (HibernateException e) {
				this.log.error("Failed to close Hibernate Session", e);
			}
		}
        */ 
            //ACO NOOP TODO
	}

	/**
	 * Create a new userAdrress if one does not exist already
	 * 
	 * @param useraddress
	 *            the address
	 * @param ownerName
	 *            name of the user whose address
	 * 
	 * @return updated useraddress
	 * 
	 * @throws EJBException
	 *             conatiner error
	 * @throws UserUnknownException
	 *             user not found
	 * @throws DuplicateUserAddressException
	 *             user already has an address
	 * 
	 * @ejb.interface-method view-type="local"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public String createUserAddress(String useraddress, String ownerName)
			throws EJBException, UserUnknownException,
			DuplicateUserAddressException {
		Session session = null;
		this.log.debug("start create useraddress");

		try {
			getUserAddress(ownerName);
			this.log.debug("user already have address assigned");
			throw new DuplicateUserAddressException(ownerName);
		} catch (UserAddressUnknownException e) {
			try {
				this.log.debug("Create useradress");
				session = HibernateLookUp.getSessionFactory().openSession();

				UserProfile owner = getUser(ownerName, session);
				setNewAddressValues(useraddress, owner);
				this.log.debug("created useraddress: "
						+ useraddress);
				publishEvent(new AddressChange(this.sessionContext
						.getCallerPrincipal().getName(), useraddress,
						AddressChange.ADDRESS_CREATED));
				session.flush();

				return useraddress;
			} catch (HibernateException hex) {
				throw new EJBException(hex);
			} finally {
				try {
					if (session != null) {
						session.close();
					}
				} catch (HibernateException hex) {
					this.log.error("Failed to close Hibernate Session", hex);
				}
			}
		}
	}

	private void setNewAddressValues(String address, UserProfile owner) {
        //TODO ACO NOOP
/*		if (address.getGUID() == null || address.getGUID().trim().equals(""))
			address.setGUID(createGUID());
		address.setCreateDate(new Date());
		address.setVersion(new Integer(1));
		address.setOwner(owner);*/
	}

	/**
	 * Load Addresses for user
	 * 
	 * @param userName
	 *            DOCUMENT ME!
	 * @param startId
	 *            DOCUMENT ME!
	 * @param fetchSize
	 *            DOCUMENT ME!
	 * @param direction
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="local"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public String[] listAddresses(String userName, long startId,
			int fetchSize, int direction) throws UserUnknownException {
		String queryString = null;

		if (direction == CursorDirection.FORWARD) {
			queryString = "where this.recordId > :startId order by recordId asc";
		}

		if (direction == CursorDirection.BACKWARD) {
			queryString = "where this.recordId < :startId order by recordId desc";
		}

		Session session = null;

		try {
			session = HibernateLookUp.getSessionFactory().openSession();

			// get user
			UserProfile user = getUser(userName, session);

			// find addresses
            /*
			Query query = session
					.createFilter(user.getAddresses(), queryString);
			query.setLong("startId", startId);
			query.setMaxResults(fetchSize);

			List list = query.list();
*/
	//		return (Address[]) list.toArray(new Address[] {});
            return user.getAliases().toArray(new String[]{});
		} catch (HibernateException e) {
			throw new EJBException(e);
		} finally {
			try {
				if (session != null) {
					session.close();
				}
			} catch (HibernateException e) {
				this.log.error("Failed to close Hibernate Session", e);
			}
		}
	}

	/**
	 * Load Addresses for user sorted by Alfa, marked by letter
	 * 
	 * @param userName
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="local"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public String[] listAddressesSortedByAlfa(String userName,
			String startWord, String endWord, int sortKey)
			throws UserUnknownException {
		String queryString = null;
		String keyField = null;
		String sortfield = null;
		switch (sortKey) {
		case ContactSortKeys.SORT_BY_FULLNAME:
			keyField = "fullName";
			sortfield = keyField;
			break;
		case ContactSortKeys.SORT_BY_NICKNAME:
			keyField = "nickName";
			sortfield = keyField;
			break;
		case ContactSortKeys.SORT_BY_ORGANIZATION_FULLNAME:
			keyField = "organization";
			sortfield = "organization, fullName";
			break;
		default:
			keyField = "fullName";
			sortfield = keyField;
			break;
		}
		queryString = "where (upper(this." + keyField + ") >= :startWord) and"
				+ "(upper(this." + keyField + ") <= :endWord) order by "
				+ sortfield;

		Session session = null;

		try {
			session = HibernateLookUp.getSessionFactory().openSession();

			// get user
			UserProfile user = getUser(userName, session);

			// find addresses
            /*
			Query query = session
					.createFilter(user.getAliases().toArray(new String()[]), queryString);
			query.setString("startWord", startWord.toUpperCase());
			query.setString("endWord", endWord.toUpperCase());

			List list = query.list();
            */
            
		//	return (Address[]) list.toArray(new Address[] {});
            return user.getAliases().toArray(new String[]{});
		} catch (HibernateException e) {
			throw new EJBException(e);
		} finally {
			try {
				if (session != null) {
					session.close();
				}
			} catch (HibernateException e) {
				this.log.error("Failed to close Hibernate Session", e);
			}
		}
	}

	/**
	 * Load Friends for user
	 * 
	 * @param userName
	 *            DOCUMENT ME!
	 * @param startId
	 *            DOCUMENT ME!
	 * @param fetchSize
	 *            DOCUMENT ME!
	 * @param direction
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="local"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public String[] listFriends(String userName) throws UserUnknownException {

        //TODO ACO NOOP
        /*
        
        String queryString = "select a.owner from " + Address.class.getName()
				+ " as a where a in (select elements(u.addresses) from "
				+ User.class.getName() + " as u where u.userName = :user)"
				+ " and a.owner.userName != :user "
				+ " and a.isUserAddress=true order by a.owner.userName asc";

		Session session = null;

		try {
			session = HibernateLookUp.getSessionFactory().openSession();

			// get user
			UserProfile user = getUser(userName, session);

			// find addresses
			Query query = session.createQuery(queryString);
			query.setParameter("user", user.getUserName());

			return (User[]) query.list().toArray(new User[] {});
		} catch (HibernateException e) {
			throw new EJBException(e);
		} finally {
			try {
				if (session != null) {
					session.close();
				}
			} catch (HibernateException e) {
				this.log.error("Failed to close Hibernate Session", e);
			}
		}
        */
        return new String[]{};
	}

	/**
	 * Load Address for user
	 * 
	 * @param userName
	 *            DOCUMENT ME!
	 * @param addressId
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * @throws RecordNotFoundException
	 *             DOCUMENT ME!
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="local"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public String getAddressById(String userName, long addressId)
			throws UserUnknownException, RecordNotFoundException {
		Session session = null;
//TODO ACO NOOP MOSTLY
/*		try {
			session = HibernateLookUp.getSessionFactory().openSession();
*/
			// get user
			UserProfile user = getUser(userName, session);
			return null;
			// find addresses
            /*
			Query query = session.createFilter(user.getAddresses(),
					"where this.recordId = :Id");
			query.setLong("Id", addressId);

			Iterator iterator = query.list().iterator();

			if (iterator.hasNext()) {
				return (Address) iterator.next();
			}

			throw new RecordNotFoundException(this.getClass().getName(),
					addressId);
                    */
	/*	} catch (HibernateException e) {
			throw new EJBException(e);
		} finally {
			try {
				if (session != null) {
					session.close();
				}
			} catch (HibernateException e) {
				this.log.error("Failed to close Hibernate Session", e);
			}
		}*/
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param userName
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * @throws UserAddressUnknownException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="local"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public String getUserAddress(String userName) throws EJBException,
			UserUnknownException, UserAddressUnknownException {
		Session session = null;
			// get user
			UserProfile user = getUser(userName, session);
			if (user != null) {
			    return user.getDefaultAlias();
            }
			throw new UserAddressUnknownException(userName);
	}

	/**
	 * Locate user by his email address.
	 * 
	 * @param emailAddress
	 * @return
	 * @throws EJBException
	 * @throws UserUnknownException
	 * @throws UserAddressUnknownException
	 * 
	 * @ejb.interface-method view-type="local"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public UserProfile getUserByEmailAddress(String emailAddress) throws EJBException,
			 UserAddressUnknownException {
	    UserProfile profile = profileService.findProfile(emailAddress);
        if (profile == null) {
            throw new UserAddressUnknownException(emailAddress);
        }
        return profile;
	}
	
	/**
	 * Retrieve the owner of an address
	 * 
	 * @param addresId
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * @throws UserAddressUnknownException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="local"
	 * @ejb.permission unchecked="true"
	 */
	public UserProfile getOwnerOf(long addresId) throws EJBException,
			UserUnknownException, UserAddressUnknownException {
	//	Session session = null;
/*
		try {
			session = HibernateLookUp.getSessionFactory().openSession();

			// find owner
			Query query = session.createQuery("select ua.owner from "
					+ Address.class.getName()
					+ " as ua where ua.recordId = :addressId");
			query.setParameter("addressId", new Long(addresId));

			Iterator iterator = query.list().iterator();

			if (iterator.hasNext()) {
				return (User) iterator.next();
			}

			throw new UserAddressUnknownException(addresId);
		} catch (HibernateException e) {
			throw new EJBException(e);
		} finally {
			try {
				if (session != null) {
					session.close();
				}
			} catch (HibernateException e) {
				this.log.error("Failed to close Hibernate Session", e);
			}
		}
        */
        //TODO ACO temporarily disabled
        return null;
	}

	/**
	 * Update addresses for user.
	 * 
	 * @param userName
	 *            DOCUMENT ME!
	 * @param address
	 *            DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="both"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public String[] updateAddresses(String userName, String[] address)
			throws EJBException, UserUnknownException, 
			UserNotAuthorizedException {
        
        //TODO ACO NOOP
        /*
		Session session = null;
		Address[] results = new Address[address.length];
        
		try {
			UserProfile user = null;
			session = HibernateLookUp.getSessionFactory().openSession();
			user = getUser(userName, session);

			for (int i = 0; i < address.length; i++) {
				results[i] = replaceAddress(user.getAddresses(), address[i]);
			}

			session.flush();
		} catch (HibernateException e) {
			throw new EJBException(e);
		} catch (UserNotAuthorizedException e) {
			this.sessionContext.setRollbackOnly();
			throw e;
		} finally {
			try {
				if (session != null) {
					session.close();
				}
			} catch (HibernateException e) {
				this.log.error("Failed to close Hibernate Session", e);
			}
		}
        */
        return new String[]{};
	}

	/**
	 * Remove addresses from user.
	 * 
	 * @param userName
	 *            DOCUMENT ME!
	 * @param addresses
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * @throws UserUnknownException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="local"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public UserProfile removeAddresses(String userName, long[] addresses)
			throws EJBException, UserUnknownException {
        /*
		Session session = null;
		boolean completed = false;

		try {
			User user = null;
			session = HibernateLookUp.getSessionFactory().openSession();
			user = getUser(userName, session);

			for (int i = 0; i < addresses.length; i++) {
				// / find address
				Iterator iterator = user.getAddresses().iterator();
				while (iterator.hasNext() && (completed == false)) {
					Address current = (Address) iterator.next();
					// got address
					if (current.getRecordId().longValue() == addresses[i]) {
						// own address
						if (current.getOwner().getUserName().equals(userName)) {
							if (!current.isIsUserAddress()) {
								// if not current users own address!
								// curent users own address can only be updated
								// this is impeartive in order to keep
								// friendships alive.
								iterator.remove();
								session.delete(current);
								publishEvent(new AddressChange(
										this.sessionContext
												.getCallerPrincipal().getName(),
										current, AddressChange.ADDRESS_DELETED));
							}
						}
						// reference
						else {
							// remove address from collection
							iterator.remove();
						}
						completed = true;
					}
				}
				completed = false;
			}

			session.flush();

			return user;
		} catch (HibernateException e) {
			throw new EJBException(e);
		} finally {
			try {
				if (session != null) {
					session.close();
				}
			} catch (HibernateException e) {
				this.log.error("Failed to close Hibernate Session", e);
			}
		}
        */
        //TODO ACO NOOP
        return null;
	}

	private Address replaceAddress(Set addresses, Address update)
			throws UserNotAuthorizedException {
        /*
		Iterator iterator = addresses.iterator();
		Address test = null;

		while (iterator.hasNext()) {
			test = (Address) iterator.next();
			this.log.info("test address id:" + test.getRecordId().longValue()
					+ "update address id:" + update.getRecordId().longValue());

			if (test.getRecordId().equals(update.getRecordId())) {
				if (!test.getOwner().getUserName().equals(
						this.sessionContext.getCallerPrincipal().getName())
						&& (!this.sessionContext.isCallerInRole("admin"))) {
					throw new UserNotAuthorizedException(this.sessionContext
							.getCallerPrincipal().getName());
				}
				this.log.debug("update address:"
						+ update.getRecordId().longValue());
				test.setFullName(update.getFullName());
				test.setNickName(update.getNickName());
				test.setOrganization(update.getOrganization());
				test.setOfficePhone(update.getOfficePhone());
				test.setHomePhone(update.getHomePhone());
				test.setMobilePhone(update.getMobilePhone());
				test.setFax(update.getFax());
				test.setEmailAddress(update.getEmailAddress());
				test.setWebURL(update.getWebURL());
				publishEvent(new AddressChange(this.sessionContext
						.getCallerPrincipal().getName(), test,
						AddressChange.ADDRESS_CHANGED));
                return test;
			}
		}
        */
        return null;
	}

	protected void publishEvent(AddressChange event) {
		String topicBindingName = "topic/addressbook";
		String tcfBindingName = "java:/JmsXA";
		publishEvent(event, topicBindingName, tcfBindingName);
	}

	/**
	 * List users.
	 * 
	 * @param searchName
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="local"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public Address[] searchContacts(String userName, String searchValue,
			int fieldKey) throws UserUnknownException, EJBException {
        return null;
        /*
		String queryString = null;
		String keyField = null;
		keyField = ContactSortKeys.getSortField(fieldKey);

		searchValue = (searchValue == null ? "" : searchValue.replace('*', '%'));
		queryString = "where this." + keyField + " like :searchValue ";

		Session session = null;

		try {
			session = HibernateLookUp.getSessionFactory().openSession();

			// get user
			User user = getUser(userName, session);

			// find addresses
			Query query = session
					.createFilter(user.getAddresses(), queryString);
			query.setString("searchValue", searchValue);

			List list = query.list();

			return (Address[]) list.toArray(new Address[] {});
		} catch (HibernateException e) {
			throw new EJBException(e);
		} finally {
			try {
				if (session != null) {
					session.close();
				}
			} catch (HibernateException e) {
				this.log.error("Failed to close Hibernate Session", e);
			}
		}
        */
        //TODO ACO NOOP
	}

	/**
	 * List users.
	 * 
	 * @param searchName
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws EJBException
	 *             DOCUMENT ME!
	 * 
	 * @ejb.interface-method view-type="local"
	 * @ejb.permission role-name="calendaruser,calendaradmin"
	 */
	public Address[] searchNewContacts(String userName, String searchName)
			throws EJBException {
        /*
		Session session = null;

		try {
			session = HibernateLookUp.getSessionFactory().openSession();

			searchName = searchName.replace('*', '%');
			// list users
			Query query = session.createQuery("select a from "
					+ Address.class.getName()
                     + " as a left join fetch a.owner "
					+ " where a.isUserAddress=true"
					+ " and a.owner.userName like :userName"
					+ " and a.owner.userName != :self and a not in "
					+ " (select elements(user.addresses) " + " from "
					+ User.class.getName()
					+ " as user where user.userName = :self) "
                     + " ");
			query.setString("userName", searchName);
			query.setString("self", userName);
			String fetchSize = (String) ServerInfo
					.getInfo(ServerInfo.SEARCH_LIMIT);
			if (fetchSize != null) {
				query.setFetchSize(Integer.parseInt(fetchSize));
			}

			return (Address[]) query.list().toArray(new Address[] {});
		} catch (HibernateException e) {
			throw new EJBException(e);
		} finally {
			try {
				if (session != null) {
					session.close();
				}
			} catch (HibernateException e) {
				this.log.error("Failed to close Hibernate Session", e);
			}
		}
        */
        //TODO ACO temporarily disabled
        return null;
	}
}