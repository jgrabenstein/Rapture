/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2011-2016 Incapture Technologies LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package reflex.function;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import reflex.IReflexHandler;
import reflex.Scope;
import reflex.debug.IReflexDebugger;
import reflex.node.BaseNode;
import reflex.node.ReflexNode;
import reflex.value.ReflexValue;

/**
 * The difference node returns those elements that are not common in the
 * parameters
 * 
 * @author amkimian
 * 
 */
public class UniqueNode extends BaseNode {
    private List<ReflexNode> exprList;

    public UniqueNode(int lineNumber, IReflexHandler handler, Scope scope, List<ReflexNode> eList) {
        super(lineNumber, handler, scope);
        this.exprList = eList;
    }

    @Override
    public ReflexValue evaluate(IReflexDebugger debugger, Scope scope) {
        debugger.stepStart(this, scope);
        // Unique([1,2,3],[2,3,4]) == [ 1,2,3,4 ] )
        Set<Object> unique = new HashSet<Object>();
        for (ReflexNode n : exprList) {
            ReflexValue param = n.evaluate(debugger, scope);
            if (param.isList()) {
                for (ReflexValue v : param.asList()) {
                    unique.add(v.asObject());
                }
            } else {
                unique.add(param.asObject());
            }
        }
        List<ReflexValue> ret = new ArrayList<ReflexValue>(unique.size());
        for (Object x : unique) {
            ret.add(new ReflexValue(x));
        }
        ReflexValue retVal = new ReflexValue(ret);
        debugger.stepEnd(this, retVal, scope);
        return retVal;
    }

    @Override
    public String toString() {
        return "unique(...)";
    }
}
