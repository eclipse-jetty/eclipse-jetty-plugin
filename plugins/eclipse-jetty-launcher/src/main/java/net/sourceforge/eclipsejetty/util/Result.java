// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package net.sourceforge.eclipsejetty.util;

import java.util.concurrent.Semaphore;

import net.sourceforge.eclipsejetty.JettyPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * A custom result type, blocking for the result
 * 
 * @author Manfred Hantschel
 * @param <RESULT_TYPE> the type of the result
 */
public class Result<RESULT_TYPE>
{

    private final Semaphore semaphore;

    private RESULT_TYPE result = null;
    private CoreException exception = null;

    /**
     * Creates the result
     */
    public Result()
    {
        super();

        semaphore = new Semaphore(0);
    }

    /**
     * Returns the result or an exception, if one was set. Blocks until a result or an exception is available
     * 
     * @return the result
     * @throws CoreException on occasion
     */
    public RESULT_TYPE getResult() throws CoreException
    {
        try
        {
            semaphore.acquire();
        }
        catch (InterruptedException e)
        {
            throw new CoreException(new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID,
                "Waiting for result got interrupted", e));
        }
        
        if (exception != null)
        {
            throw exception;
        }

        return result;
    }

    public void setResult(RESULT_TYPE result)
    {
        this.result = result;

        semaphore.release();
    }

    public void setException(CoreException exception)
    {
        this.exception = exception;

        semaphore.release();
    }

}
