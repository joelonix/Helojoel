package com.ingenico.base;

/*
$HeadURL: https://svn.ingenico.com/SPO/Dev/E2E/trunk/src/com/ingenico/base/Log4jTestNGAppender.java $
$Id: Log4jTestNGAppender.java 16127 2015-12-09 10:24:11Z haripraks $
 */

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.testng.Reporter;

/**
 * log4j TestNG appender
 */
@SuppressWarnings("serial")
@Plugin(name="TestNGAppender", category="Core", elementType="appender", printObject=true)
public  final class Log4jTestNGAppender extends AbstractAppender {

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    public static  Locale locale = Locale.ENGLISH;

    protected Log4jTestNGAppender(String name, Filter filter,
            Layout<? extends Serializable> layout, final boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    /**
     * Overriding append method to customize the log information
     */
    @Override
    public void append(LogEvent event) {
        readLock.lock();
        try {
           	SimpleDateFormat dateFormat = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]",locale);
			Date time = Calendar.getInstance().getTime();
			StringBuffer buffer = new StringBuffer(dateFormat.format(time));
			Reporter.log(buffer.toString()+event.getLevel()+": "+event.getMessage().getFormat().toString());
        } catch (Exception ex) {
            if (!ignoreExceptions()) {
                throw new AppenderLoggingException(ex);
            }
        } finally {
            readLock.unlock();
        }
    }

  /**
   * Plugin factory used to generate the pattern as per the log4j2 implementation
   * @param name
   * @param layout
   * @param filter
   * @param otherAttribute
   * @return
   */
    @PluginFactory
    public static Log4jTestNGAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layoutparam,
            @PluginElement("Filter") final Filter filter,
            @PluginAttribute("otherAttribute") String otherAttribute) {
    		Layout<? extends Serializable> layoutParameter;
        if (name == null) {
            LOGGER.error("No name provided for Log4jTestNGAppender");
            return null;
        }
        if (layoutparam == null) {
        	layoutParameter=PatternLayout.createDefaultLayout();
        }
        else{
        	layoutParameter=layoutparam;
        }
        return new Log4jTestNGAppender(name, filter, layoutParameter, true);
    }
}