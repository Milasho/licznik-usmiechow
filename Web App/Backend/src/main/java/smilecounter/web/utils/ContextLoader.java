package smilecounter.web.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

@ApplicationScoped
public class ContextLoader {
    public static final String EXTERNAL_PROPERTIES = "java:global/pathToProperties";

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private Context ctx;

    @PostConstruct
    public void init(){
        try {
            ctx  = new InitialContext();
        } catch (NamingException e) {
            LOGGER.error("Error during initializing context",  e);
        }
    }

    public String loadStringFromContext(String name){
        String result = null;
        try {
            result = (String) ctx.lookup(name);
        }
        catch(NameNotFoundException e){
            LOGGER.debug("Name {} was not found.", name);
        }
        catch (NamingException e) {
            LOGGER.error("Error during loading param {}", name, e);
        }
        return result;
    }
}
