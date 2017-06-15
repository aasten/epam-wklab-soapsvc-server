package com.epam.wklab.soap.dao;

import com.epam.wklab.person.Person;

//import java.util.logging.Logger;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

/**
 * Created by sten on 15.06.17.
 */
public class LoggingPersonDAO implements PersonDAO {
    private final static Logger logger = Logger.getLogger(LoggingPersonDAO.class);
    @Override
    public void store(Person p) {
        if(logger.isInfoEnabled()) {
            JAXBContext context = null;
            StringWriter out = new StringWriter();
            try {
                context = JAXBContext.newInstance(Person.class);
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marshaller.marshal(p, out);
            } catch (JAXBException e) {
                logger.error(e);
            }
            String xml = out.toString();
            logger.info(xml);
        }
    }
}
