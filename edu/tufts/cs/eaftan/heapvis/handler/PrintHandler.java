/****************************************************************************
 * This is a handler class to be used with the hprof parser.  For each 
 * record the parser encounters, it parses the record and calls the
 * matching function in its RecordHandler class.  The RecordHandler 
 * handles each record, performing some function such as printing the
 * record or building a graph.
 *
 * This derived class prints details for each record encountered.
 * **************************************************************************/

package edu.tufts.cs.eaftan.heapvis.handler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import edu.tufts.cs.eaftan.heapvis.parser.datastructures.*;

public class PrintHandler extends RecordHandler {

  private HashMap<Long, String> stringMap = new HashMap<Long, String>();
  private HashMap<Long, ClassInfo> classMap = new HashMap<Long, ClassInfo>();

  /* handler for file header */
  
  public void header(String format, int idSize, long time) {
    System.out.println(format);
    System.out.println(idSize);
    System.out.println(millisecondsDateToString(time));
  }
  

  /* Handlers for top-level records */

  public void stringInUTF8(long id, String data) {
    // store string for later lookup
    stringMap.put(id, data);
  }

  public void loadClass(int classSerialNum, long classObjId, 
      int stackTraceSerialNum, long classNameStringId) {
    System.out.println("Load Class:");
    System.out.println("    class serial num: " + classSerialNum);
    System.out.println("    class object id: " + classObjId);
    System.out.println("    stack trace serial num: " + stackTraceSerialNum);
    System.out.println("    class name string: " + stringMap.get(classNameStringId));
  }

  public void unloadClass(int classSerialNum) {
    System.out.println("Unload Class:");
    System.out.println("    class serial num: " + classSerialNum);
  }

  public void stackFrame(long stackFrameId, long methodNameStringId, 
      long methodSigStringId, long sourceFileNameStringId, 
      int classSerialNum, int location) {
    System.out.println("Stack Frame:");
    System.out.println("    stack frame id: " + stackFrameId);
    System.out.println("    method name string: " + stringMap.get(methodNameStringId));
    System.out.println("    method sig string: " + stringMap.get(methodSigStringId));
    System.out.println("    source file name string: " + stringMap.get(sourceFileNameStringId));
    System.out.println("    class serial num: " + classSerialNum);
    System.out.print("    location: ");
    switch (location) {

      case 0:
        System.out.println("no line information available");
        break;

      case -1: 
        System.out.println("unknown location");
        break;

      case -2:
        System.out.println("compiled method");
        break;

      case -3:
        System.out.println("native method");
        break;

      default:
        System.out.println("line number " + location);
        break;

    }

  }

  public void stackTrace(int stackTraceSerialNum, int threadSerialNum, 
      int numFrames, long[] stackFrameIds) {
    System.out.println("Stack Trace:");
    System.out.println("    stack trace serial num: " + stackTraceSerialNum);
    System.out.println("    thread serial num: " + threadSerialNum);
    System.out.println("    num frames: " + numFrames);
    System.out.println("    stack frame ids:");
    for (long sfi: stackFrameIds) {
      System.out.println("        " + sfi);
    }
  }

  public void allocSites(short bitMaskFlags, float cutoffRatio, 
      int totalLiveBytes, int totalLiveInstances, long totalBytesAllocated,
      long totalInstancesAllocated, AllocSite[] sites) {
    System.out.println("Alloc Sites:");
    System.out.println("    bit mask flags: " + bitMaskFlags);
    System.out.println("    incremental vs. complete: " + testBitMask(bitMaskFlags, 0x1));
    System.out.println("    sorted by allocation vs. line: " + testBitMask(bitMaskFlags, 0x2));
    System.out.println("    whether to force GC: " + testBitMask(bitMaskFlags, 0x4));
    System.out.println("    cutoff ratio: " + cutoffRatio);
    System.out.println("    total live bytes: " + totalLiveBytes);
    System.out.println("    total live instances: " + totalLiveInstances);
    System.out.println("    total bytes allocated: " + totalBytesAllocated);
    System.out.println("    total instances allocated: " + totalInstancesAllocated);
    for (int i=0; i<sites.length; i++) {
      System.out.println("        alloc site " + (i+1) + ":");
      System.out.print("            array indicator: ");
      if (sites[i].arrayIndicator == 0) {
        System.out.println("not an array");
      } else {
        System.out.println("array of " + getBasicType(sites[i].arrayIndicator));
      }
      System.out.println("            class serial num: " + sites[i].classSerialNum); 
      System.out.println("            stack trace serial num: " + sites[i].stackTraceSerialNum); 
      System.out.println("            num live bytes: " + sites[i].numLiveBytes); 
      System.out.println("            num live instances: " + sites[i].numLiveInstances); 
      System.out.println("            num bytes allocated: " + sites[i].numBytesAllocated); 
      System.out.println("            num instances allocated: " + sites[i].numInstancesAllocated); 
    }
  }

  public void heapSummary(int totalLiveBytes, int totalLiveInstances,
      long totalBytesAllocated, long totalInstancesAllocated) {
    System.out.println("Heap Summary:");
    System.out.println("    total live bytes: " + totalLiveBytes);
    System.out.println("    total live instances: " + totalLiveInstances);
    System.out.println("    total bytes allocated: " + totalBytesAllocated);
    System.out.println("    total instances allocated: " + totalInstancesAllocated);
  }

  public void startThread(int threadSerialNum, long threadObjectId,
      int stackTraceSerialNum, long threadNameStringId, long threadGroupNameId,
      long threadParentGroupNameId) {
    System.out.println("Start Thread:");
    System.out.println("    thread serial num: " + threadSerialNum);
    System.out.println("    thread object id: " + threadObjectId);
    System.out.println("    stack trace serial num: " + stackTraceSerialNum);
    System.out.println("    thread name string: " + stringMap.get(threadNameStringId));
    System.out.println("    thread group name id: " + stringMap.get(threadGroupNameId));
    System.out.println("    thread parent group name id: " + stringMap.get(threadParentGroupNameId));
  }

  public void endThread(int threadSerialNum) {
    System.out.println("End Thread:");
    System.out.println("    thread serial num: " + threadSerialNum);
  }

  public void heapDump() {
    System.out.println("Heap Dump:");
  }
  
  public void heapDumpEnd() {
    System.out.println("Heap Dump End:");
  }

  public void heapDumpSegment() {
    System.out.println("Heap Dump Segment:");
  }

  public void cpuSamples(int totalNumOfSamples, CPUSample[] samples) {
    System.out.println("CPU Samples:");
    System.out.println("    total num of samples: " + totalNumOfSamples);
    for (int i=0; i<samples.length; i++) {
      System.out.println("        cpu sample " + (i+1) + ":");
      System.out.println("            number of samples: " + samples[i].numSamples);
      System.out.println("            stack trace serial num: " + samples[i].stackTraceSerialNum);
    }
  }

  public void controlSettings(int bitMaskFlags, short stackTraceDepth) {
    System.out.println("Control Settings:");
    System.out.println("    bit mask flags: " + bitMaskFlags);
    System.out.println("    alloc traces on/off: " + testBitMask(bitMaskFlags, 0x1));
    System.out.println("    cpu sampling on/off: " + testBitMask(bitMaskFlags, 0x2));
    System.out.println("    stack trace depth: " + stackTraceDepth);
  }


  /* Handlers for heap dump records */

  public void rootUnknown(long objId) {
    System.out.println("Root Unknown:");
    System.out.println("    object id: " + objId);
  }

  public void rootJNIGlobal(long objId, long JNIGlobalRefId) {
    System.out.println("Root JNI Global:");
    System.out.println("    object id: " + objId);
    System.out.println("    JNI global ref id: " + JNIGlobalRefId);
  }

  public void rootJNILocal(long objId, int threadSerialNum, int frameNum) {
    System.out.println("Root JNI Local:");
    System.out.println("    object id: " + objId);
    System.out.println("    thread serial num: " + threadSerialNum);
    System.out.println("    frame num: " + frameNum);
  }

  public void rootJavaFrame(long objId, int threadSerialNum, int frameNum) {
    System.out.println("Root Java Frame:");
    System.out.println("    object id: " + objId);
    System.out.println("    thread serial num: " + threadSerialNum);
    System.out.println("    frame num: " + frameNum);
  }

  public void rootNativeStack(long objId, int threadSerialNum) {
    System.out.println("Root Native Stack:");
    System.out.println("    object id: " + objId);
    System.out.println("    thread serial num: " + threadSerialNum);
  }

  public void rootStickyClass(long objId) {
    System.out.println("Root Sticky Class:");
    System.out.println("    object id: " + objId);
  }

  public void rootThreadBlock(long objId, int threadSerialNum) {
    System.out.println("Root Thread Block:");
    System.out.println("    object id: " + objId);
    System.out.println("    thread serial num: " + threadSerialNum);
  }

  public void rootMonitorUsed(long objId) {
    System.out.println("Root Monitor Used:");
    System.out.println("    object id: " + objId);
  }

  public void rootThreadObj(long objId, int threadSerialNum, 
      int stackTraceSerialNum) {
    System.out.println("Root Thread Object:");
    System.out.println("    object id: " + objId);
    System.out.println("    thread serial num: " + threadSerialNum);
    System.out.println("    stack trace serial num: " + stackTraceSerialNum);
  }

  public void classDump(long classObjId, int stackTraceSerialNum, 
      long superClassObjId, long classLoaderObjId, long signersObjId,
      long protectionDomainObjId, long reserved1, long reserved2, 
      int instanceSize, Constant[] constants, Static[] statics,
      InstanceField[] instanceFields) {
    System.out.println("Class Dump:");
    System.out.println("    class object id: " + classObjId);
    System.out.println("    stack trace serial num: " + stackTraceSerialNum);
    System.out.println("    super class object id: " + superClassObjId);
    System.out.println("    class loader object id: " + classLoaderObjId);
    System.out.println("    signer's object id: " + signersObjId);
    System.out.println("    protection domain object id: " + protectionDomainObjId);
    System.out.println("    reserved 1: " + reserved1);
    System.out.println("    reserved 2: " + reserved2);
    System.out.println("    instance size: " + instanceSize);
    
    if (constants != null) {
      System.out.println("    constant pool:");
      for (Constant c: constants) {
        System.out.println("        " + c.constantPoolIndex + ": " + c.value);
      }
    }
    
    if (statics != null) {
      System.out.println("    static fields:");
      for (Static s: statics) {
        System.out.println("        " + stringMap.get(s.staticFieldNameStringId) + ": " + 
            s.value);
      }
    }
    
    if (instanceFields != null) {
      System.out.println("    instance fields:");
      for (InstanceField i: instanceFields) {
        System.out.println("        " + stringMap.get(i.fieldNameStringId) + ": " + 
            i.type);
      }
    }
    
    // store class info in a hashmap for later access
    classMap.put(classObjId, new ClassInfo(classObjId, superClassObjId, instanceSize,
        instanceFields));
  }

  public void instanceDump(long objId, int stackTraceSerialNum, 
      long classObjId, Value[] instanceFieldValues) {
    System.out.println("Instance Dump:");
    System.out.println("    object id: " + objId);
    System.out.println("    stack trace serial num: " + stackTraceSerialNum);
    System.out.println("    class object id: " + classObjId);
    
    if (instanceFieldValues != null) {
      System.out.println("    instance field values:");
      
      // superclass of Object is 0
      int i = 0;
      long nextClass = classObjId;
      while (nextClass != 0) {
        ClassInfo ci = classMap.get(nextClass);
        nextClass = ci.superClassObjId;
        if (ci.instanceFields != null) {
          for (InstanceField field: ci.instanceFields) {
            System.out.print("        " + stringMap.get(field.fieldNameStringId));
            System.out.println(" = " + instanceFieldValues[i]);
            i++;
          }
        }
      }
      assert i == instanceFieldValues.length;
    }
  }

  public void objArrayDump(long objId, int stackTraceSerialNum, 
      long elemClassObjId, long[] elems) {
    System.out.println("Object Array Dump:");
    System.out.println("    object id: " + objId);
    System.out.println("    stack trace serial num: " + stackTraceSerialNum);
    System.out.println("    element class object id: " + elemClassObjId);

    if (elems != null) {
      for (int i=0; i<elems.length; i++) {
        System.out.println("        element " + (i+1) + ": " + elems[i]);
      }
    }
  }

  public void primArrayDump(long objId, int stackTraceSerialNum, 
      byte elemType, Value[] elems) {
    System.out.println("Primitive Array Dump:");
    System.out.println("    object id: " + objId);
    System.out.println("    stack trace serial num: " + stackTraceSerialNum);
    System.out.print("    number of elements: ");
    if (elems != null) {
      System.out.println(elems.length);
    } else {
      System.out.println("0");
    }
    System.out.println("    element type: " + getBasicType(elemType));
  
    if (elems != null) {
      for (int i=0; i<elems.length; i++) {
        System.out.println("        element " + (i+1) + ": " + elems[i]);
      }
    }

  } 


  /* Utility methods */

  private static boolean testBitMask(int bitMaskFlags, int mask) {
    if ((bitMaskFlags & mask) != 0) 
      return true;
    else
      return false;
  }

  private static String getBasicType(byte type) {
    switch (type) {
      case 2:
        return "object";
      case 4:
        return "boolean";
      case 5:
        return "char";
      case 6:
        return "float";
      case 7:
        return "double";
      case 8:
        return "byte";
      case 9:
        return "short";
      case 10:
        return "int";
      case 11:
        return "long";
      default:
        return null;
    }
  }
  
  private static String millisecondsDateToString(long milliseconds) {

    SimpleDateFormat formatter = 
        new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");

    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(milliseconds);

    return formatter.format(calendar.getTime());
  }


}