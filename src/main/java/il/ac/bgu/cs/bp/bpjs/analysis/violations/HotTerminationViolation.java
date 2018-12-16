/*
 * The MIT License
 *
 * Copyright 2018 michael.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package il.ac.bgu.cs.bp.bpjs.analysis.violations;

import il.ac.bgu.cs.bp.bpjs.analysis.DfsTraversalNode;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.joining;

/**
 *
 * @author michael
 */
public class HotTerminationViolation extends Violation {
    
    private final Set<String> hotlyTerminated;

    public HotTerminationViolation(Set<String> hotlyTerminated, List<DfsTraversalNode> counterExampleTrace) {
        super(counterExampleTrace);
        this.hotlyTerminated = hotlyTerminated;
    }

    @Override
    public String decsribe() {
        return "Hot Termination - The following b-threads were hot when the b-program ended: " +
                hotlyTerminated.stream().sorted().collect(joining(", "));
    }

    public Set<String> getBThreadNames() {
        return hotlyTerminated;
    }
    
    
}