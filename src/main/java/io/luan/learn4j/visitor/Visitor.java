package io.luan.learn4j.visitor;

import io.luan.learn4j.structure.impl.*;

/**
 * This is the base interface for a Graph Visitor
 * <p>
 * New Visitors should extend BaseVisitor
 *
 * @author Guangmiao Luan
 * @since 31/08/2017.
 */
public interface Visitor {

    void visitAbs(Abs abs, Object... params);

    void visitAdd(Add node, Object... params);

    void visitAssign(Assign node, Object... params);

    void visitConstant(Constant node, Object... params);

    void visitDivide(Divide node, Object... params);

    void visitFill(Fill node, Object... params);

    void visitGroup(Group node, Object... params);

    void visitMatMul(MatMul node, Object... params);

    void visitMultiply(Multiply node, Object... params);

    void visitNegate(Negate node, Object... params);

    void visitParameter(Parameter node, Object... params);

    void visitPower(Power node, Object... params);

    void visitReduceMean(ReduceMean node, Object... params);

    void visitReduceSum(ReduceSum reduceSum, Object... params);

    void visitSigmoid(Sigmoid node, Object... params);

    void visitSigmoidGrad(SigmoidGrad node, Object... params);

    void visitSign(Sign sign, Object... params);

    void visitSquare(Square node, Object... params);

    void visitStep(Step step, Object... params);

    void visitSubtract(Subtract node, Object... params);

    void visitVariable(Variable node, Object... params);

    void visitRelu(Relu node, Object... params);

}
