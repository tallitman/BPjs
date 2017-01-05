/*
 *  Author: Michael Bar-Sinai
 */
package il.ac.bgu.cs.bp.bpjs.mains;

import il.ac.bgu.cs.bp.bpjs.bprogram.runtimeengine.BProgram;
import il.ac.bgu.cs.bp.bpjs.bprogram.runtimeengine.listeners.StreamLoggerListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Scriptable;

/**
 * Run a single program file. Outputs event log to (@code stdout}.
 * 
 * @author michael
 */
public class RunFile {
    
    public static void main(String[] args) {
        if ( args.length == 0 ) {
            printUsageAndExit();
        }
        
        try {
            BProgram bpp = new BProgram("BPjs"){
                @Override
                protected void setupProgramScope(Scriptable scope) {
                    for ( String arg : args ) {
                        if ( arg.equals("-") ) {
                            println(" [READ] stdin");
                            try {
                                evaluateInGlobalScope(System.in, "stdin");
                            } catch ( EvaluatorException ee ) {
                                logScriptExceptionAndQuit(ee, arg);
                            }
                        } else {
                            Path inFile = Paths.get(arg);
                            println( " [READ] %s", inFile.toAbsolutePath().toString());
                            if ( ! Files.exists(inFile) ) {
                                println("File %s does not exit", inFile.toAbsolutePath().toString() );
                                System.exit(-2);
                            }
                            try ( InputStream in = Files.newInputStream(inFile) ) {
                                evaluateInGlobalScope(in, arg);
                            } catch ( EvaluatorException ee ) {
                                logScriptExceptionAndQuit(ee, arg);
                            } catch (IOException ex) {
                                println("Exception while processing " + arg + ": " + ex.getMessage());
                                Logger.getLogger(RunFile.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        println(" [ OK ] %s", arg);
                    }
                }

                private void logScriptExceptionAndQuit(EvaluatorException ee, String arg) {
                    println( "Error in source %s:", arg);
                    println( ee.details() );
                    println( "line: " + ee.lineNumber() + ":" + ee.columnNumber() );
                    println( "source: " + ee.lineSource() );
                    System.exit(-3);
                }
            };
            
           bpp.addListener( new StreamLoggerListener() );
           bpp.start();
            
        } catch (InterruptedException ex) {
            Logger.getLogger(RunFile.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
    }
    
    private static void println(String template, String... params) {
        print( template + "\n", params );
    }
    
    private static void print(String template, String... params) {
        if (params.length == 0 ) {
            System.out.print("# " + template);
        } else {
            System.out.printf("# " + template, (Object[])params);
        }
    }
    
    private static void printUsageAndExit() {
        try (BufferedReader rdr = new BufferedReader( new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("RunFile-usage.txt")))) {
            rdr.lines().forEach(System.out::println);
        } catch (IOException ex) {
            throw new RuntimeException("Cannot find 'RunFile-usage.txt'");
        }
        System.exit(-1);
    }
    
}
