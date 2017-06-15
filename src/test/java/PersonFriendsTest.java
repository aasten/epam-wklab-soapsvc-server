import com.epam.wklab.person.Friends;
import com.epam.wklab.person.Person;
import com.epam.wklab.soap.api.PersonFriendsIface;
import com.epam.wklab.soap.dao.PersonDAO;
import com.epam.wklab.soap.simpl.NoMatchedFriendsException;
import com.epam.wklab.soap.simpl.PersonFriends;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

/**
 * Created by sten on 12.06.17.
 */
public class PersonFriendsTest {

    private static PersonFriendsIface testedService;

    @BeforeClass
    public static void prepareTestClass() {
        PersonDAO mockedPersonDAO = mock(PersonDAO.class);
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Person got = invocation.getArgument(0);
                {
                    JAXBContext context = null;
                    StringWriter out = new StringWriter();
                    try {
                        context = JAXBContext.newInstance(Person.class);
                        Marshaller marshaller = context.createMarshaller();
                        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                        marshaller.marshal(got, out);
                    } catch (JAXBException e) {
                        System.err.println(e);
                    }
                    String xml = out.toString();
                    System.out.println(xml);
                }
                return null;
            }
        }).when(mockedPersonDAO).store(any(Person.class));
        testedService = new PersonFriends(mockedPersonDAO);
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

    @Test
    public void getFriends_exampleContains_Returned() {
        final Integer year = new Integer(1989);
        Person friend = preparePerson("K", "yyyyMMdd", year.toString() + "0707");
        Person personToTest = preparePersonWithFriend(friend);
        Friends sampleFriends = new Friends();
        sampleFriends.getPerson().add(friend);
        try {
            assertThat(testedService.getFriends(personToTest, year).getPerson(),is(sampleFriends.getPerson()));
        } catch (NoMatchedFriendsException e) {
            fail("Unexpected exception is thrown: " + e.toString());
        }
    }
}
