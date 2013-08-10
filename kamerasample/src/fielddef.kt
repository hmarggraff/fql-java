package org.funql.ri.test.genericobject


enum class Types {string; int; float; bool; date; ref; obj; array; lid} // lid == local id. not a full ref, but one wich is relative to some ntry point
class FieldDef(val name: String, val typ: Types)
{
    class object {
        fun str(name: String) = FieldDef(name, Types.string)
        fun int(name: String) = FieldDef(name, Types.int)
        fun float(name: String) = FieldDef(name, Types.float)
        fun bool(name: String) = FieldDef(name, Types.bool)
        fun date(name: String) = FieldDef(name, Types.date)
        fun ref(name: String) = FieldDef(name, Types.ref)
        fun obj(name: String) = FieldDef(name, Types.obj)
        fun id(name: String) = FieldDef(name, Types.lid)
        fun arr(name: String) = FieldDef(name, Types.array)
    }
}

class TypeDef(val name: String, vararg fieldsarg: FieldDef)
{
    val fields: Array<FieldDef> = fieldsarg
}

class Lid(val target: Any)
class Ref(val target: Any, val container: String)

class TestObject(val typ: TypeDef, vararg valuesarg: Any?)
{
    val values: Array<Any?> = valuesarg
    val oid : Long = genOid

    fun forEach(f: (Any?) -> Unit) {
        values.forEach(f)
    }

    fun get(ix:Int) = values[ix]

    class object {
        var genOid: Long = 1
            get() = $genOid++
    }

}
