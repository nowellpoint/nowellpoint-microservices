package com.nowellpoint.registration;

import java.util.TimeZone;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.undertow.WARArchive;
import org.wildfly.swarm.swagger.SwaggerArchive;

public class Main {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        
        //
		// build the container
		//
		
        Swarm container = new Swarm(args);
                
        //
        // set default time zone to UTC
        //
        
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        
        //
        // create the JAX-RS deployment archive
        // 
        
        WARArchive deployment = ShrinkWrap.create(WARArchive.class)
        		.addPackage(Package.getPackage("com.nowellpoint.api"))
        		.addPackage(Package.getPackage("com.nowellpoint.registration"))
//        		.addAsResource(new ClassLoaderAsset("index.html", Main.class.getClassLoader()), "index.html")
//        		.addAsWebInfResource(new ClassLoaderAsset("web.xml", Main.class.getClassLoader()), "web.xml")
//        		.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
        		.addAllDependencies();
        
        //
        // enable swagger
        //
        
        final SwaggerArchive archive = deployment.as(SwaggerArchive.class);
        archive.setResourcePackages("com.nowellpoint.api");
        archive.setPrettyPrint(Boolean.TRUE);

        //
        // start the container and deploy the archive
        //
        
        container.start().deploy(deployment);
    }
}