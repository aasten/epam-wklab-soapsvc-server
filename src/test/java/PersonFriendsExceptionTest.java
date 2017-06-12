import com.epam.wklab.person.Friends;
import com.epam.wklab.person.Person;
import com.epam.wklab.soap.api.PersonFriendsIface;
import com.epam.wklab.soap.simpl.NoMatchedFriendsException;
import com.epam.wklab.soap.simpl.PersonFriends;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by sten on 12.06.17.
 */
public class PersonFriendsExceptionTest {

    private static PersonFriendsIface testedService;

    @BeforeClass
    public static void prepareTestClass() {
        testedService = new PersonFriends();
    }

    private Person preparePerson(String name, String birthdayFormat, String birthday) {
        Person p = new Person();
        p.setName(name);
        Date birthDate = null;
        try {
            birthDate = (new SimpleDateFormat(birthdayFormat)).parse(birthday);
        } catch (ParseException e) {
            throw new RuntimeException("Test composing error, parse error of " + birthday + " as " + birthdayFormat,e);
        }
        {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(birthDate);
            try {
                XMLGregorianCalendar xc = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
                p.setBirth(xc);
            } catch (DatatypeConfigurationException e) {
                throw new RuntimeException("Test composing error, calendar instantiation error", e);
            }
        }
        return p;
    }

    private Person preparePersonWithFriend(Person friend) {
        Person p = preparePerson("L", "yyyyMMdd","19870419");
        p.setFriends(new Friends());
        p.getFriends().getPerson().add(friend);
        return p;
    }

    @Test(expected = NoMatchedFriendsException.class)
    public void getFriends_exampleNotContains_Throws() throws NoMatchedFriendsException {
        final Integer year = new Integer(1989);
        final Integer wrongYear = new Integer(1987);
        Person friend = preparePerson("K", "yyyyMMdd", year.toString() + "0707");
        Person personToTest = preparePersonWithFriend(friend);
        Friends sampleFriends = new Friends();
        sampleFriends.getPerson().add(friend);
        testedService.getFriends(personToTest, wrongYear);
        fail("Exception instance of NoMatchedFriendsException should be thrown");
    }
}
