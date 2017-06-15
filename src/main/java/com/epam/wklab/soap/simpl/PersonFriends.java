package com.epam.wklab.soap.simpl;

import com.epam.wklab.person.Friends;
import com.epam.wklab.person.Person;
import com.epam.wklab.soap.api.PersonFriendsIface;
import com.epam.wklab.soap.dao.LoggingPersonDAO;
import com.epam.wklab.soap.dao.PersonDAO;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * Created by sten on 11.04.17.
 */
@WebService
public class PersonFriends implements PersonFriendsIface {

    private PersonDAO personDAO = new LoggingPersonDAO();

    public void setPersonDAO(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    public PersonFriends() {}

    public PersonFriends(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    @WebMethod
    public Friends getFriends(Person p, Integer year) throws NoMatchedFriendsException {

        if(null == personDAO) {
            throw new NoMatchedFriendsException(new IllegalStateException("Internal error: service was not prepared"));
        }
        personDAO.store(p);

        Friends matchedFriends = new Friends();
        for(Person f : p.getFriends().getPerson()) {
            if(f.getBirth().getYear() == year) {
                matchedFriends.getPerson().add(f);
            }
        }
        if(matchedFriends.getPerson().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("No matched friends of person \"");
            sb.append(p.getName());
            sb.append("\" which were born in year ");
            sb.append(Integer.toString(year));
            throw new NoMatchedFriendsException(sb.toString());
        }
        return matchedFriends;
    }
}
