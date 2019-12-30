package io.github.bananapuncher714.cartographer.core;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;

import io.github.bananapuncher714.cartographer.core.module.Module;
import io.github.bananapuncher714.cartographer.core.module.ModuleDescription;
import io.github.bananapuncher714.cartographer.core.module.ModuleLoader;
import io.github.bananapuncher714.cartographer.core.util.BukkitUtil;

public class ModuleManager {
	protected Cartographer plugin;
	protected Map< String, Module > modules = new HashMap< String, Module >();
	protected File moduleFolder;
	
	protected ModuleManager( Cartographer plugin, File moduleFolder ) {
		this.plugin = plugin;
		this.moduleFolder = moduleFolder;
		moduleFolder.mkdirs();
	}
	
	protected void loadModules() {
		for ( File file : moduleFolder.listFiles() ) {
			if ( file.isDirectory() ) {
				continue;
			}
			
			Module module = loadModule( file );
			registerModule( module );
		}
	}
	
	public void registerModule( Module module ) {
		modules.put( module.getName(), module );
	}
	
	public void registerAndEnable( Module module ) {
		registerModule( module );
		module.setEnabled( true );
	}
	
	public Module loadModule( File file ) {
		ModuleDescription description = ModuleLoader.getDescriptionFor( file );
		Module module = ModuleLoader.load( file, description );
		
		File dataFolder = new File( moduleFolder + "/" + description.getName() );
		module.load( plugin, description, dataFolder );
		
		return module;
	}
	
	public Module getModule( String name ) {
		return modules.get( name );
	}
	
	public Set< Module > getModules() {
		Set< Module > moduleSet = new HashSet< Module >();
		moduleSet.addAll( modules.values() );
		return moduleSet;
	}
	
	public boolean enableModule( Module module ) {
		Validate.notNull( module );
		if ( module.isEnabled() ) {
			return false;
		}
		// Check for their dependencies
		ModuleDescription description = module.getDescription();
		boolean allDependenciesLoaded = true;
		StringBuilder missingDeps = new StringBuilder();
		for ( String dependency : description.getDependencies() ) {
			if ( !BukkitUtil.isPluginLoaded( dependency ) ) { 
				allDependenciesLoaded = false;
				missingDeps.append( dependency );
				missingDeps.append( " " );
			}
		}
		if ( allDependenciesLoaded ) {
			plugin.getLogger().info( "[ModuleManager] Enabling " + description.getName() + " v" + description.getVersion() + " by " + description.getAuthor() );
			module.setEnabled( true );
		} else {
			plugin.getLogger().warning( "[ModuleManager] Unable to enable " + description.getName() + " due to the missing dependencies: " + missingDeps.toString().trim() );
		}
		return true;
	}
	
	public boolean disableModule( Module module ) {
		Validate.notNull( module );
		if ( module.isEnabled() ) {
			ModuleDescription description = module.getDescription();
			plugin.getLogger().info( "[ModuleManager] Disabling " + description.getName() + " v" + description.getVersion() + " by " + description.getAuthor() );
			module.setEnabled( false );
			return true;
		}
		return false;
	}
	
	public void enableModules() {
		// Load all modules, but check if their dependencies are loaded
		// This should be called after the server is done loading.
		// Essentially, all plugins *should* be loaded by this time
		for ( Module module : modules.values() ) {
			enableModule( module );
		}
	}
	
	public void disableModules() {
		for ( Module module : modules.values() ) {
			disableModule( module );
		}
	}
	
	protected void unloadModules() {
		for ( String id : modules.keySet() ) {
			Module module = modules.get( id );
			module.unload();
			
			plugin.getLogger().info( "[ModuleManager] Unloading " + id );
		}
	}
}
