/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015 Red Hat Inc. and/or its affiliates and other
 * contributors as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.test.arquillian.ce.processserver;

import org.jboss.arquillian.ce.api.OpenShiftResource;
import org.jboss.arquillian.ce.api.OpenShiftResources;
import org.jboss.arquillian.ce.api.Template;
import org.jboss.arquillian.ce.api.TemplateParameter;
import org.jboss.arquillian.ce.cube.RouteURL;
import org.jboss.arquillian.ce.shrinkwrap.Libraries;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;

/**
 * @author Filippe Spolti
 */

@RunWith(Arquillian.class)
@Template(url = "https://raw.githubusercontent.com/${template.repository}/application-templates/${template.branch}/processserver/processserver63-mysql-s2i.json",
        parameters = {
                @TemplateParameter(name = "KIE_SERVER_USER", value = "${kie.username:kieserver}"),
                @TemplateParameter(name = "KIE_SERVER_PASSWORD", value = "${kie.password:Redhat@123}")
        }
)
@OpenShiftResources({
        @OpenShiftResource("https://raw.githubusercontent.com/${template.repository}/application-templates/${template.branch}/secrets/processserver-app-secret.json")
})
public class ProcessServerMySqlTest extends LibraryProcessTestBase {


        @Deployment
        public static WebArchive getDeployment() throws Exception {
                WebArchive war = getDeploymentInternal();
                war.addPackage("org.openshift.quickstarts.processserver.library.types");
                war.addClass(LibraryClient.class);
                war.addClass(LibraryProcessTestBase.class);
                return war;
        }

        @RouteURL("kie-app")
        private URL routeURL;

        @Override
        protected URL getRouteURL() {
                return routeURL;
        }

        @Test
        @RunAsClient
        public void testProcessServerCapabilities() throws Exception {
                checkKieServerCapabilities(getRouteURL(), "BPM");
        }

        @Test
        @RunAsClient
        public void testProcessServerContainer() throws Exception {
                checkKieServerContainer("processserver-library=org.openshift.quickstarts:processserver-library:1.3.0.Final");
        }

}
