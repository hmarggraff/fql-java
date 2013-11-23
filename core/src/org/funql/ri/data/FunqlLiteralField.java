package org.funql.ri.data;

import org.funql.ri.util.NamedImpl;

import java.lang.String;

/**
 * Created by hmf on 23.11.13.
 */
public abstract class FunqlLiteralField  extends NamedImpl implements FunqlField {
    public FunqlLiteralField(String name) {
	super(name);
    }

    @Override
    public FunqlTypeDef getSubType() {
	return null;
    }
    public static class IntField extends FunqlLiteralField {
	public IntField(String name) {
	    super(name);
	}

	@Override
	public FunqlBasicType getType() {
	    return FunqlBasicType.Integer;
	}
    }
    public static class RealField extends FunqlLiteralField {
	public RealField(String name) {
	    super(name);
	}

	@Override
	public FunqlBasicType getType() {
	    return FunqlBasicType.Real;
	}
    }
    public static class StringField extends FunqlLiteralField {
	public StringField(String name) {
	    super(name);
	}

	@Override
	public FunqlBasicType getType() {
	    return FunqlBasicType.String;
	}
    }
    public static class BooleanField extends FunqlLiteralField {
	public BooleanField(String name) {
	    super(name);
	}

	@Override
	public FunqlBasicType getType() {
	    return FunqlBasicType.Boolean;
	}
    }
    public static class DateField extends FunqlLiteralField {
	public DateField(String name) {
	    super(name);
	}

	@Override
	public FunqlBasicType getType() {
	    return FunqlBasicType.Date;
	}
    }
    public static class BinaryField extends FunqlLiteralField {
	public BinaryField(String name) {
	    super(name);
	}

	@Override
	public FunqlBasicType getType() {
	    return FunqlBasicType.Binary;
	}
    }
}
