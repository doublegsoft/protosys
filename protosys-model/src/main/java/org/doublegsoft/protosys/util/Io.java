/*
 * Copyright 2016 doublegsoft.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.doublegsoft.protosys.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.*;
import java.util.Map;

/**
 * The utility for IO.
 *
 * @author <a href="mailto:guo.guo.gan@gmail.com">Christian Gann</a>
 * @since 1.0
 */
public class Io {

  private final static Configuration FREEMARKER = new Configuration(Configuration.getVersion());

  private final static VelocityEngine VELOCITY = new VelocityEngine();

  static {
    FREEMARKER.setClassForTemplateLoading(Io.class, "/");

    VELOCITY.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
    VELOCITY.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
  }

  /**
   * @param rootDir
   * @param filename
   * @return
   * @throws IOException
   */
  public static File create(File rootDir, String filename) throws IOException {
    File retVal = new File(rootDir, filename);
    File parent = retVal.getParentFile();
    if (!parent.exists()) {
      parent.mkdirs();
    }
    retVal.createNewFile();
    return retVal;
  }

  public static void copy(InputStream src, File dst) throws IOException {
    File parent = dst.getParentFile();
    if (!parent.exists()) {
      parent.mkdirs();
    }
    try (FileOutputStream fos = new FileOutputStream(dst)) {
      byte[] buf = new byte[4096];
      int len = 0;
      while ((len = src.read(buf)) != -1) {
        fos.write(buf, 0, len);
      }
      fos.flush();
    }
  }

  public static void freemarker(String templatePath, Map<String, Object> dataModel, File destFile) throws IOException {
    Template tpl = FREEMARKER.getTemplate(templatePath, "UTF-8");
    File parent = destFile.getParentFile();
    if (!parent.exists()) {
      parent.mkdirs();
    }
    OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(destFile), "UTF-8");
    try {
      tpl.process(dataModel, fw);
    } catch (TemplateException ex) {
      throw new IOException(ex);
    }
    fw.flush();
    fw.close();
  }

  public static String freemarker(String templatePath, Map<String, Object> dataModel) throws IOException {
    Template tpl = FREEMARKER.getTemplate(templatePath, "UTF-8");
    StringWriter retVal = new StringWriter();
    try {
      tpl.process(dataModel, retVal);
    } catch (TemplateException ex) {
      throw new IOException(ex);
    }
    retVal.flush();
    retVal.close();
    return retVal.toString();
  }

  public static void velocity(String templatePath, Map<String, Object> dataModel, File destFile) throws IOException {
    File parent = destFile.getParentFile();
    if (!parent.exists()) {
      parent.mkdirs();
    }
    VelocityContext ctx = new VelocityContext();
    dataModel.entrySet().forEach((e) -> {
      ctx.put(e.getKey(), e.getValue());
    });
    org.apache.velocity.Template template = VELOCITY.getTemplate(templatePath, "UTF-8");
    OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(destFile), "UTF-8");
    template.merge(ctx, writer);
    writer.flush();
    writer.close();
  }

  public static String velocity(String templatePath, Map<String, Object> dataModel) throws IOException {
    VelocityContext ctx = new VelocityContext();
    dataModel.entrySet().forEach((e) -> {
      ctx.put(e.getKey(), e.getValue());
    });
    org.apache.velocity.Template template = VELOCITY.getTemplate(templatePath, "UTF-8");
    StringWriter writer = new StringWriter();
    template.merge(ctx, writer);
    writer.flush();
    writer.close();
    return writer.toString();
  }

  public static void write(String content, String filepath) throws IOException {
    write(content, new File(filepath));
  }

  public static void write(String content, File file) throws IOException {
    if (!file.getParentFile().exists()) {
      file.getParentFile().mkdirs();
    }
    OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
    fw.write(content);
    fw.flush();
    fw.close();
  }

  public static String indent(int indent, String content) {
    if (content == null) {
      return "";
    }
    String[] lines = content.split("\\r|\\n");
    StringHolder retVal = new StringHolder();
    for (String line : lines) {
      if (line.trim().isEmpty()) {
        continue;
      }
      retVal.indent(indent).append(line).linefeed();
    }
    return retVal.toString();
  }

  private Io() {

  }

}
