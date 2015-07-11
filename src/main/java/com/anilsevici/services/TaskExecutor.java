package com.anilsevici.services;

import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener implementation class TaskExecutor
 *
 */
@WebListener
public class TaskExecutor implements ServletContextListener {

    /**
     * Default constructor. 
     */
	private ScheduledExecutorService scheduler;
	
    public TaskExecutor() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce)  { 
         // TODO Auto-generated method stub
    	scheduler.shutdownNow();
    	System.out.println("finish");
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce)  { 
         // TODO Auto-generated method stub
    	scheduler = Executors.newSingleThreadScheduledExecutor();
        try {
			scheduler.scheduleAtFixedRate(new FetchJob(), 0, 5, TimeUnit.MINUTES);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("success");
    }
	
}
