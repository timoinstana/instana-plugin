package jenkins.plugins.instana;

import java.io.File;

import javax.annotation.Nonnull;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.XmlFile;
import hudson.init.InitMilestone;
import hudson.init.Initializer;
import hudson.util.FormValidation;
import hudson.util.Secret;
import hudson.util.XStream2;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import jenkins.plugins.instana.util.HttpRequestNameValuePair;


@Extension
public class InstanaPluginGlobalConfig extends GlobalConfiguration {

	private @Nonnull String instanaUrl;
	private @Nonnull Secret token;
	private @Nonnull HttpMode httpMode = HttpMode.POST;

	static final String RELEASES_API = "/api/releases";

    private static final XStream2 XSTREAM2 = new XStream2();

    public InstanaPluginGlobalConfig() {
		instanaUrl = "";
    	token = Secret.fromString("");
    	load();
    }

    @Initializer(before = InitMilestone.PLUGINS_STARTED)
    public static void xStreamCompatibility() {
        XSTREAM2.addCompatibilityAlias("ReleaseEvent$DescriptorImpl", InstanaPluginGlobalConfig.class);
        XSTREAM2.addCompatibilityAlias("jenkins.plugins.instana.util.NameValuePair", HttpRequestNameValuePair.class);
    }

    @Override
    protected XmlFile getConfigFile() {
        Jenkins j = Jenkins.get();
        File rootDir = j.getRootDir();
        File xmlFile = new File(rootDir, "ReleaseEvent.xml");
        return new XmlFile(XSTREAM2, xmlFile);
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) {
        instanaUrl = json.getString("instanaUrl");
        token = Secret.fromString(json.getString("token"));
        save();
        return true;
    }

	public static FormValidation validateKeyName(String value) {
		return FormValidation.validateRequired(value);
	}

    public static InstanaPluginGlobalConfig get() {
        return GlobalConfiguration.all().get(InstanaPluginGlobalConfig.class);
    }

	@Nonnull
	public String getInstanaUrl() {
		return instanaUrl;
	}

	public void setInstanaUrl(@Nonnull String instanaUrl) {
		this.instanaUrl = instanaUrl;
	}

	@Nonnull
	public Secret getToken() {
		return token;
	}

	public void setToken(@Nonnull Secret token) {
		this.token = token;
	}

	@Nonnull
	public HttpMode getHttpMode() {
		return httpMode;
	}

	public void setHttpMode(@Nonnull HttpMode httpMode) {
		this.httpMode = httpMode;
	}

}
