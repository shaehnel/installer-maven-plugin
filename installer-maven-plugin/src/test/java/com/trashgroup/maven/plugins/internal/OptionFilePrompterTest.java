package com.trashgroup.maven.plugins.internal;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Properties;

@RunWith(MockitoJUnitRunner.class)
public class OptionFilePrompterTest {

    @Mock
    private Log logger;

    @Mock
    private Prompter prompter;

    @Test
    public void mustThrowExceptionWhenFileDoesnotExist() {
        OptionFilePrompter optionFilePrompter = new OptionFilePrompter(this.getClass().getResource(".").getPath(), prompter, logger);
        final Properties properties = optionFilePrompter.processFile("doesnotexist.properties");
        Assert.assertEquals(0, properties.size());
    }

    @Test
    public void mustReadExistingFile() throws PrompterException {
        Mockito.when(prompter.prompt(Mockito.anyString(), Mockito.anyString())).then(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                // return 2nd argument (default value)
                return (String) args[1];
            }
        });
        OptionFilePrompter optionFilePrompter = new OptionFilePrompter(this.getClass().getResource("/").getPath(), prompter, logger);
        final Properties properties = optionFilePrompter.processFile("options.properties");

        Mockito.verify(prompter).prompt(Mockito.anyString(), Mockito.anyString());
        Assert.assertEquals("value1", properties.getProperty("key1"));

    }
}