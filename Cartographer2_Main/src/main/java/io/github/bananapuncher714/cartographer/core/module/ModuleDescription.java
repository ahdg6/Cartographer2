package io.github.bananapuncher714.cartographer.core.module;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.Validate;

public class ModuleDescription {
	private static final Pattern VALID_NAME = Pattern.compile( "^[A-Za-z0-9_.-]+$" );
	
	protected final String name;
	protected final String main;
	protected final String author;
	protected String description;
	protected final String version;
	protected String website;
	protected final Set< String > dependencies = new HashSet< String >();
	protected final File file;
	
	public ModuleDescription( File jarFile, String name, String main, String author, String version ) {
		if ( !VALID_NAME.matcher( name ).matches() ) {
			throw new IllegalArgumentException( "name " + name + " contains invalid characters." );
		}
		Validate.notNull( jarFile );
		Validate.notNull( name );
		
		this.file = jarFile;
		this.name = name;
		this.main = main;
		this.author = author;
		this.version = version;
	}
	
	public File getFile() {
		return file;
	}

	public String getMain() {
		return main;
	}
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public String getAuthor() {
		return author;
	}

	public String getVersion() {
		return version;
	}

	public String getWebsite() {
		return website;
	}

	public void setDescription( String description ) {
		this.description = description;
	}

	public void setWebsite( String website ) {
		this.website = website;
	}

	public Set< String > getDependencies() {
		return dependencies;
	}
}
