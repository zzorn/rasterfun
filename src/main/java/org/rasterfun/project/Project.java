package org.rasterfun.project;

import java.io.File;

/**
 * A project contains a number of PictureGenerators that are being worked on,
 * as well as other assets used in them, e.g. links to images.
 *
 * A project can be built, in which case it generates the pictures for its picture generators and saves them
 * on specified paths, or under the directory with the project file by default.
 */
public interface Project {



    /**
     * Generates all pictures in the project, and saves them to the specified output directory.
     */
    void build();

    /**
     * Saves the whole project to the specified file, also including any assets used in it.
     * @param file location to export to.
     */
    void exportProject(File file);
}
