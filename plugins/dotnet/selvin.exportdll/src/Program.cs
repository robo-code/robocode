using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Reflection;
using System.Text.RegularExpressions;
using selvin.exportdllattribute;

namespace selvin.exportdll
{
    internal enum ParserState
    {
        Normal,
        ClassDeclaration,
        Class,
        DeleteExportDependency,
        MethodDeclaration,
        MethodProperties,
        Method,
        DeleteExportAttribute,
    }

    public class Program
    {
        private static bool FindWithAttribute(MemberInfo mi, object obj)
        {
            object[] attrs = mi.GetCustomAttributes(typeof (ExportDllAttribute), false);
            if (attrs.Length > 0)
                return true;
            return false;
        }

        public static int Main(string[] args)
        {
            try
            {
                if (args.Length < 1)
                {
                    Console.WriteLine("Parameter error!");
                    return 1;
                }

                string ildasmpath = @"C:\Program Files\Microsoft SDKs\Windows\v6.1\Bin\ildasm.exe";
                string ilasmpath = @"C:\WINDOWS\Microsoft.NET\Framework\v2.0.50727\ilasm.exe";
                bool debug = false;
                if (args.Length > 1)
                {
                    ildasmpath = args[1];
                }
                if (args.Length > 2)
                {
                    ilasmpath = args[2];
                }
                if (args.Length > 3)
                {
                    if (args[3] == "/Debug")
                        debug = true;
                }
                string filepath = Path.GetFullPath(args[0]);
                string path = Path.GetDirectoryName(filepath);
                string ext = Path.GetExtension(filepath);
                if (ext != ".dll")
                {
                    Console.WriteLine("Target should be dll!");
                    return 1;
                }
                int exportscount = 0;
                var dic = new Dictionary<string, Dictionary<string, KeyValuePair<string, string>>>();
                byte[] rawassembly;
                using (FileStream fs = File.OpenRead(filepath))
                {
                    var br = new BinaryReader(fs);
                    rawassembly = br.ReadBytes((int) fs.Length);
                    br.Close();
                }
                Assembly assembly = Assembly.Load(rawassembly);
                Type[] types = assembly.GetTypes();
                foreach (Type type in types)
                {
                    MemberInfo[] mis = type.FindMembers(MemberTypes.All, BindingFlags.Public | BindingFlags.Static,
                                                        FindWithAttribute, null);
                    {
                        foreach (MemberInfo mi in mis)
                        {
                            object[] attrs = mi.GetCustomAttributes(typeof (ExportDllAttribute),
                                                                    false);
                            if (attrs.Length > 0)
                            {
                                var attr = attrs[0] as ExportDllAttribute;
                                if (!dic.ContainsKey(type.FullName))
                                    dic[type.FullName] = new Dictionary<string, KeyValuePair<string, string>>();
                                dic[type.FullName][mi.Name] = new KeyValuePair<string, string>(attr.ExportName,
                                                                                               attr.CallingConvention);
                                exportscount++;
                            }
                        }
                    }
                }
                if (exportscount > 0)
                {
                    int exportpos = 1;
                    string filename = Path.GetFileNameWithoutExtension(filepath);
                    Directory.SetCurrentDirectory(path);
                    var proc = new Process();
                    string arguments = string.Format("/nobar{1}/out:{0}.il {0}.dll", filename,
                                                     debug ? " /linenum " : " ");
                    Console.WriteLine("Deassembly file with arguments '{0}'", arguments);
                    var info = new ProcessStartInfo(ildasmpath, arguments);
                    info.UseShellExecute = false;
                    info.CreateNoWindow = false;
                    info.RedirectStandardOutput = true;
                    proc.StartInfo = info;
                    proc.Start();
                    proc.WaitForExit();
                    Console.WriteLine(proc.StandardOutput.ReadToEnd());
                    if (proc.ExitCode != 0)
                        return proc.ExitCode;
                    var wholeilfile = new List<string>();
                    StreamReader sr = File.OpenText(Path.Combine(path, filename + ".il"));
                    string methoddeclaration = "";
                    string methodname = "";
                    string classdeclaration = "";
                    string methodbefore = "";
                    string methodafter = "";
                    int methodpos = 0;
                    var classnames = new Stack<string>();
                    var externassembly = new List<string>();
                    ParserState state = ParserState.Normal;
                    while (!sr.EndOfStream)
                    {
                        string line = sr.ReadLine();
                        string trimedline = line.Trim();
                        bool addilne = true;
                        switch (state)
                        {
                            case ParserState.Normal:
                                if (trimedline.StartsWith(".corflags"))
                                {
                                    wholeilfile.Add(".corflags 0x00000002");
                                    wholeilfile.Add(string.Format(".vtfixup [{0}] int32 fromunmanaged at VT_01",
                                                                  exportscount));
                                    wholeilfile.Add(string.Format(".data VT_01 = int32[{0}]", exportscount));
                                    Console.WriteLine("Adding vtfixup.");
                                    addilne = false;
                                }
                                else if (trimedline.StartsWith(".class"))
                                {
                                    state = ParserState.ClassDeclaration;
                                    addilne = false;
                                    classdeclaration = trimedline;
                                }
                                else if (trimedline.StartsWith(".assembly extern 'selvin.exportdllattribute"))
                                {
                                    addilne = false;
                                    state = ParserState.DeleteExportDependency;
                                    Console.WriteLine("Deleting ExportDllAttribute dependency.");
                                }
                                break;
                            case ParserState.DeleteExportDependency:
                                if (trimedline.StartsWith("}"))
                                {
                                    state = ParserState.Normal;
                                }
                                addilne = false;
                                break;
                            case ParserState.ClassDeclaration:
                                if (trimedline.StartsWith("{"))
                                {
                                    state = ParserState.Class;
                                    string classname = "";
                                    var reg = new Regex(@".+\s+([^\s]+) extends \[.*");
                                    Match m = reg.Match(classdeclaration);
                                    if (m.Groups.Count > 1)
                                        classname = m.Groups[1].Value;
                                    classname = classname.Replace("'", "");
                                    if (classnames.Count > 0)
                                        classname = classnames.Peek() + "+" + classname;
                                    classnames.Push(classname);
                                    Console.WriteLine("Found class: " + classname);
                                    wholeilfile.Add(classdeclaration);
                                }
                                else
                                {
                                    classdeclaration += " " + trimedline;
                                    addilne = false;
                                }
                                break;
                            case ParserState.Class:
                                if (trimedline.StartsWith(".class"))
                                {
                                    state = ParserState.ClassDeclaration;
                                    addilne = false;
                                    classdeclaration = trimedline;
                                }
                                else if (trimedline.StartsWith(".method"))
                                {
                                    if (dic.ContainsKey(classnames.Peek()))
                                    {
                                        methoddeclaration = trimedline;
                                        addilne = false;
                                        state = ParserState.MethodDeclaration;
                                    }
                                }
                                else if (trimedline.StartsWith("} // end of class"))
                                {
                                    classnames.Pop();
                                    if (classnames.Count > 0)
                                        state = ParserState.Class;
                                    else
                                        state = ParserState.Normal;
                                }
                                break;
                            case ParserState.MethodDeclaration:
                                if (trimedline.StartsWith("{"))
                                {
                                    var reg =
                                        new Regex(@"(?<before>[^\(]+(\(\s[^\)]+\))*\s)(?<method>[^\(]+)(?<after>\(.*)");
                                    Match m = reg.Match(methoddeclaration);
                                    if (m.Groups.Count > 3)
                                    {
                                        methodbefore = m.Groups["before"].Value;
                                        methodafter = m.Groups["after"].Value;
                                        methodname = m.Groups["method"].Value;
                                    }
                                    Console.WriteLine("Found method: " + methodname);
                                    if (dic[classnames.Peek()].ContainsKey(methodname))
                                    {
                                        methodpos = wholeilfile.Count;
                                        state = ParserState.MethodProperties;
                                    }
                                    else
                                    {
                                        wholeilfile.Add(methoddeclaration);
                                        state = ParserState.Method;
                                        methodpos = 0;
                                    }
                                }
                                else
                                {
                                    methoddeclaration += " " + trimedline;
                                    addilne = false;
                                }
                                break;
                            case ParserState.Method:
                                if (trimedline.StartsWith("} // end of method"))
                                {
                                    state = ParserState.Class;
                                }
                                break;
                            case ParserState.MethodProperties:
                                if (trimedline.StartsWith(".custom instance void ['selvin.exportdllattribute"))
                                {
                                    addilne = false;
                                    state = ParserState.DeleteExportAttribute;
                                }
                                else if (trimedline.StartsWith("// Code"))
                                {
                                    state = ParserState.Method;
                                    if (methodpos != 0)
                                        wholeilfile.Insert(methodpos, methoddeclaration);
                                }
                                break;
                            case ParserState.DeleteExportAttribute:
                                if (trimedline.StartsWith(".custom") || trimedline.StartsWith("// Code"))
                                {
                                    KeyValuePair<string, string> attr = dic[classnames.Peek()][methodname];
                                    if (methodbefore.Contains("marshal( "))
                                    {
                                        int pos = methodbefore.IndexOf("marshal( ");
                                        methodbefore = methodbefore.Insert(pos, "modopt([mscorlib]" + attr.Value + ") ");
                                        methoddeclaration = methodbefore + methodname + methodafter;
                                    }
                                    else
                                        methoddeclaration = methodbefore + "modopt([mscorlib]" + attr.Value + ") " +
                                                            methodname + methodafter;
                                    Console.WriteLine("\tChanging calling convention: " + attr.Value);
                                    if (methodpos != 0)
                                        wholeilfile.Insert(methodpos, methoddeclaration);
                                    if (methodname == "DllMain")
                                        wholeilfile.Add(" .entrypoint");
                                    wholeilfile.Add(".vtentry 1 : " + exportpos);
                                    wholeilfile.Add(string.Format(".export [{0}] as {1}", exportpos,
                                                                  dic[classnames.Peek()][methodname].Key));
                                    Console.WriteLine("\tAdding .vtentry:{0} .export:{1}", exportpos,
                                                      dic[classnames.Peek()][methodname].Key);
                                    exportpos++;
                                    state = ParserState.Method;
                                }
                                else
                                    addilne = false;
                                break;
                        }
                        if (addilne)
                            wholeilfile.Add(line);
                    }
                    sr.Close();
                    StreamWriter sw = File.CreateText(Path.Combine(path, filename + ".il"));
                    foreach (string line in wholeilfile)
                    {
                        sw.WriteLine(line);
                    }
                    sw.Close();
                    string res = filename + ".res";
                    if (File.Exists(filename + ".res"))
                        res = " /resource=" + res;
                    else
                        res = "";
                    proc = new Process();
                    arguments = string.Format("/nologo /quiet /out:{0}.dll {0}.il /DLL{1} {2}", filename, res,
                                              debug ? "/debug" : "/optimize");
                    Console.WriteLine("Compiling file with arguments '{0}'", arguments);
                    info = new ProcessStartInfo(ilasmpath, arguments);
                    info.UseShellExecute = false;
                    info.CreateNoWindow = false;
                    info.RedirectStandardOutput = true;
                    proc.StartInfo = info;
                    proc.Start();
                    proc.WaitForExit();
                    Console.WriteLine(proc.StandardOutput.ReadToEnd());
                    if (proc.ExitCode != 0)
                        return proc.ExitCode;
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return -1;
            }
            return 0;
        }
    }
}