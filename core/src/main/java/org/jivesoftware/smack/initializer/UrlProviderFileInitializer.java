package org.jivesoftware.smack.initializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jivesoftware.smack.provider.ProviderFileLoader;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.FileUtils;

/**
 * Loads the provider file defined by the URL returned by {@link #getFilePath()}.  This file will be loaded on Smack initialization.
 * 
 * @author Robin Collier
 *
 */
public abstract class UrlProviderFileInitializer implements SmackInitializer {
    private static final Logger log = Logger.getLogger(UrlProviderFileInitializer.class.getName());

    private List<Exception> exceptions = new LinkedList<Exception>();

    @Override
    public void initialize() {
        String filePath = getFilePath();
        
        try {
            InputStream is = FileUtils.getStreamForUrl(filePath, getClassLoader());
            
            if (is != null) {
                log.log(Level.INFO, "Loading providers for file [" + filePath + "]");
                ProviderFileLoader pfl = new ProviderFileLoader(is);
                ProviderManager.getInstance().addLoader(pfl);
                exceptions.addAll(pfl.getLoadingExceptions());
            }
            else {
                log.log(Level.WARNING, "No input stream created for " + filePath);
                exceptions.add(new IOException("No input stream created for " + filePath));
            }
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "Error trying to load provider file " + filePath, e);
            exceptions.add(e);
        }
    }

    @Override
    public List<Exception> getExceptions() {
    	return Collections.unmodifiableList(exceptions);
    }

    protected abstract String getFilePath();
    
    /**
     * Returns an array of class loaders to load resources from.
     *
     * @return an array of ClassLoader instances.
     */
    protected ClassLoader getClassLoader() {
        return null;
    }
}
