package com.epam.wklab.soap.dao;

import com.epam.wklab.person.Person;

/**
 * Created by sten on 14.06.17.
 */
public interface PersonDAO {

    public class DAOException extends RuntimeException {
        public DAOException(Throwable cause) { super(cause); }
    }

    public void store(Person p) throws DAOException;
}
