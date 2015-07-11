package com.anilsevici.accountik;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class LdapAccounting {

	private String useruri;
	private String password;
	private final String url = "ldap://localhost:389";

	public LdapAccounting(String useruri, String password) {
		this.useruri = useruri;
		this.password = password;
	}

	public void connect() throws Exception {
		Hashtable<String, String> env = config();
		DirContext ctx = new InitialDirContext(env);

	}

	private Hashtable<String, String> config() {

		Hashtable<String, String> env = new Hashtable<String, String>();
		StringBuilder s = new StringBuilder();

		s.append("uid=");
		s.append(useruri);
		s.append(",");
		s.append("ou=People,dc=maxcrc,dc=com");

		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, url);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, s.toString());
		env.put(Context.SECURITY_CREDENTIALS, password);

		return env;

	}

}
