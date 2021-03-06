/**
 * This file is part of Eclipse Steady.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.psr.vulas.python.virtualenv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Set;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.sap.psr.vulas.FileAnalysisException;
import com.sap.psr.vulas.python.ProcessWrapperException;
import com.sap.psr.vulas.python.pip.PipInstalledPackage;
import com.sap.psr.vulas.shared.categories.Slow;
import com.sap.psr.vulas.shared.json.model.ConstructId;
import com.sap.psr.vulas.shared.util.FileUtil;
import com.sap.psr.vulas.shared.util.StringList;

public class VirtualenvWrapperTest {

	/**
	 * Attention: Runs long...
	 * 
	 * @throws IllegalArgumentException
	 * @throws ProcessWrapperException
	 * @throws FileAnalysisException
	 */
	@Test
	@Category(Slow.class)
	public void testCreateVirtualenv() throws IllegalArgumentException, ProcessWrapperException, FileAnalysisException {

		// Create virtualenv
		final Path project = Paths.get("src", "test", "resources", "cf-helloworld");
		final VirtualenvWrapper vew = new VirtualenvWrapper(project);
		final Path ve_path = vew.getPathToVirtualenv();
		assertTrue(FileUtil.isAccessibleDirectory(ve_path));

		// Get packages
		final Set<PipInstalledPackage> packs = vew.getInstalledPackages();
		assertEquals(8, packs.size());
		
		// Get rid of the project itself
		final Set<PipInstalledPackage> filtered_packs = PipInstalledPackage.filterUsingArtifact(packs,  new StringList().add("cf-helloworld"), false);
		assertEquals(7, filtered_packs.size());
		
		// Get SHA1 for every package
		for(PipInstalledPackage p: filtered_packs) {
			final String sha1 = p.getDigest();
			assertTrue(sha1!=null && !sha1.equals(""));
		}

		// Get constructs for every package
		for(PipInstalledPackage p: filtered_packs) {
			final Collection<ConstructId> constructs = p.getLibrary().getConstructs();
			assertTrue(constructs!=null && constructs.size()>0);
		}
	}
}
