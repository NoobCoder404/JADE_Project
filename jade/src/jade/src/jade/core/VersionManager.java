package jade.core;

class VersionManager {
	
	public String getVersion() {
		String version = "4.3.0"; // The $ surrounded Version keyword is automatically replaced by the target doTag of build.xml
		return version;
	}
	
	public String getRevision() {
		String revision = "6664"; // The $ surrounded WCREV keyword is automatically replaced by WCREV with subversion
		return revision;
	}
	
	public String getDate() {
		String date = "2013/03/27 09:34:17"; // The $ surrounded WCDATE keyword is automatically replaced by WCREV with subversion
		return date;
	}
}
