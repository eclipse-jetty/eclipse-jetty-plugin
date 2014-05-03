package net.sourceforge.eclipsejetty;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

public class JettyPluginUtilsTest {

  @Test
  public void toRelativePathInProject() {
    checkToRelativePath("/worspace/my-project/src/main/webapp", "src/main/webapp");
  }

  @Test
  public void toRelativePathOutsideProject() {
    checkToRelativePath("/my-project/src/main/webapp", "../../my-project/src/main/webapp");
  }

  private void checkToRelativePath(String path, String result) {
    IProject project = createProject();

    String relativePath = JettyPluginUtils.toRelativePath(project, path);

    assertThat(relativePath, is(result));
  }

  private IProject createProject() {
    IProject project = mock(IProject.class);
    IPath projectLocation = new Path("/worspace/my-project");
    when(project.getLocation()).thenReturn(projectLocation);
    IPath projectFullPath = new Path("/my-project");
    when(project.getFullPath()).thenReturn(projectFullPath);
    return project;
  }

}
