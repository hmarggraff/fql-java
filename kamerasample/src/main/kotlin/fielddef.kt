package org.funql.ri.test.genericobject


enum class Types {string; int; float; bool; date; ref; obj; array}
class FieldDef(val name: String, val typ: Types)
{
    class object {
        fun str(val name: String) = FieldDef(name, Types.string)
        fun int(val name: String) = FieldDef(name, Types.int)
        fun float(val name: String) = FieldDef(name, Types.float)
        fun bool(val name: String) = FieldDef(name, Types.bool)
        fun date(val name: String) = FieldDef(name, Types.date)
        fun ref(val name: String) = FieldDef(name, Types.ref)
        fun obj(val name: String) = FieldDef(name, Types.obj)
        fun arr(val name: String) = FieldDef(name, Types.array)
    }
}

class TypeDef(val name: String, vararg fieldsarg: FieldDef)
{
    val fields: Array<FieldDef> = fieldsarg
}

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
