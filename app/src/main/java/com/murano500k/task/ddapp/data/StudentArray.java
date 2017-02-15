package com.murano500k.task.ddapp.data;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

import java.util.List;


@NamespaceList({
        @Namespace(reference="http://schemas.datacontract.org/2004/07/DDApp.SFA.Api.Controllers"),
        @Namespace(reference="http://www.w3.org/2001/XMLSchema-instance", prefix="i")
})
@Root(name = "TestController.Student")
public class StudentArray {

    @ElementList(name = "ArrayOfTestController.Student")
    private List<Student> students;

    public List<Student> getStudents() {
        return students;
    }
}
