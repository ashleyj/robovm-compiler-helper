/*
 * Copyright (C) 2014 Trillian Mobile AB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.robovm.compilerhelper;

import org.apache.maven.plugin.MojoExecutionException;
import org.jboss.shrinkwrap.resolver.api.NoResolvedResultException;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.robovm.compiler.Version;

import java.io.File;
import java.io.IOException;

public class RoboVMResolver {

    private static String ROBOVM_DIST = "org.robovm:robovm-dist:tar.gz:" + getRoboVMVersion();
    private static String JFX_BACKPORT = "net.java.openjfx.backport:openjfx-78-backport:1.8.0-ea-b96.1";
    private static String JFX_BACKPORT_COMPAT = "net.java.openjfx.backport:openjfx-78-backport-compat:1.8.0.1";
    private static String JFX_NATIVE = "net.java.openjfx.backport:openjfx-78-backport-native:1.8.0-ea-b96.1";

    protected static String getRoboVMVersion() {
        return Version.getVersion();
    }

    public File resolveArtifact(String artifact) {
        File f;
        try {
            /* do offline check first */
            f = Maven.configureResolver().workOffline().resolve(artifact).withoutTransitivity().asSingleFile();
        } catch (NoResolvedResultException nre) {
            f = Maven.configureResolver()
                    .withRemoteRepo("Sonatype Nexus Snapshots","https://oss.sonatype.org/content/repositories/snapshots/", "default")
                    .resolve(artifact).withoutTransitivity().asSingleFile();
        }
        return f;
    }

    protected File[] resolveArtifacts(String artifact) {
        File[] f;
        try {
            /* do offline check first */
            f = Maven.configureResolver().workOffline().resolve(artifact).withTransitivity().asFile();
        } catch (NoResolvedResultException nre) {
            f = Maven.configureResolver()
                    .withRemoteRepo("Sonatype Nexus Snapshots","https://oss.sonatype.org/content/repositories/snapshots/", "default")
                    .resolve(artifact).withTransitivity().asFile();
        }
        return f;
    }

    protected File resolveRoboVMCompilerArtifact() {
        return resolveArtifact(ROBOVM_DIST);
    }

    protected File resolveJavaFXBackportRuntimeArtifact() {
        return resolveArtifact(JFX_BACKPORT);
    }

    protected File resolveJavaFXBackportCompatibilityArtifact() {
        return resolveArtifact(JFX_BACKPORT_COMPAT);
    }

    protected File resolveJavaFXNativeArtifact() {
        return resolveArtifact(JFX_NATIVE);
    }

    protected File unpackJavaFXNativeIOSArtifact() throws IOException {
        File jarFile = resolveJavaFXNativeArtifact();
        // by default unpack into the local repo directory
        File unpackBaseDir = new File(jarFile.getParent(), "unpacked");
        unpack(jarFile, unpackBaseDir);
        return unpackBaseDir;
    }

    private GenericLogger getLog() {
        return new GenericLogger();
    }

    protected File unpack(File archive, File targetDirectory)
            throws IOException {

        if (!targetDirectory.exists()) {

            getLog().info("Extracting '" + archive + "' to: " + targetDirectory);
            if (!targetDirectory.mkdirs()) {
                throw new RuntimeException(
                        "Unable to create base directory to unpack into: "
                                + targetDirectory);
            }

            Archiver.unarchive(archive, targetDirectory);
        } else {
            getLog().debug(
                    "Archive '" + archive + "' was already unpacked in: "
                            + targetDirectory);
        }

        return targetDirectory;
    }

    public File unpackInPlace(File jarFile) throws MojoExecutionException, IOException {
        File unpackDir = new File(jarFile.getParent(), "unpacked");
        return unpack(jarFile, unpackDir);
    }

    public static String getROBOVM_DIST() {
        return ROBOVM_DIST;
    }

    public static void setROBOVM_DIST(String ROBOVM_DIST) {
        RoboVMResolver.ROBOVM_DIST = ROBOVM_DIST;
    }

    public static String getJFX_BACKPORT() {
        return JFX_BACKPORT;
    }

    public static void setJFX_BACKPORT(String JFX_BACKPORT) {
        RoboVMResolver.JFX_BACKPORT = JFX_BACKPORT;
    }

    public static String getJFX_BACKPORT_COMPAT() {
        return JFX_BACKPORT_COMPAT;
    }

    public static void setJFX_BACKPORT_COMPAT(String JFX_BACKPORT_COMPAT) {
        RoboVMResolver.JFX_BACKPORT_COMPAT = JFX_BACKPORT_COMPAT;
    }

    public static String getJFX_NATIVE() {
        return JFX_NATIVE;
    }

    public static void setJFX_NATIVE(String JFX_NATIVE) {
        RoboVMResolver.JFX_NATIVE = JFX_NATIVE;
    }
}
