package io.luan.learn4j.visitor.impl;

import io.luan.learn4j.structure.Expression;
import io.luan.learn4j.structure.factory.ExpressionFactory;
import io.luan.learn4j.structure.impl.*;
import io.luan.learn4j.utils.ShapeUtils;
import lombok.val;

import static io.luan.learn4j.structure.factory.ExpressionFactory.*;

/**
 * @author Guangmiao Luan
 * @since 06/10/2017.
 */
public class ReverseGradientVisitor extends BaseVisitor {

    public ReverseGradientVisitor() {

    }

    @Override
    public void visitAbs(Abs node, Object... params) {
        Expression grad = getGradientOrDefault(node, params);

        String gradName = node.getName() + "/grad_" + node.getBase().getName();

        String signName = gradName + "/sign";
        Expression sign = createSign(signName, node.getBase());
        Expression mul = createMultiply(gradName, grad, sign);

        node.getBase().setGradient(node, mul);

        node.getBase().accept(this, mul);
    }

    @Override
    public void visitAdd(Add node, Object... params) {
        Expression grad = getGradientOrDefault(node, params);

        String leftGradName = node.getName() + "/grad_" + node.getLeft().getName();
        String rightGradName = node.getName() + "/grad_" + node.getRight().getName();

        val pair = ShapeUtils.getReductionIndices(node.getLeft().getShape(), node.getRight().getShape());

        Expression leftGrad = createReduceSum(leftGradName, grad, pair.getLeft());
        Expression rightGrad = createReduceSum(rightGradName, grad, pair.getRight());

        node.getLeft().setGradient(node, leftGrad);
        node.getRight().setGradient(node, rightGrad);

        node.getLeft().accept(this, leftGrad);
        node.getRight().accept(this, rightGrad);
    }

    @Override
    public void visitDivide(Divide node, Object... params) {
        Expression grad = getGradientOrDefault(node, params);

        String leftGradName = node.getName() + "/grad_" + node.getLeft().getName();
        String rightGradName = node.getName() + "/grad_" + node.getRight().getName();

        val pair = ShapeUtils.getReductionIndices(node.getLeft().getShape(), node.getRight().getShape());

        Expression leftGrad = createDivide(leftGradName, grad, node.getRight());
        Expression rightGrad = createDivide(rightGradName, createNegate("", node.getLeft()), node.getRight());
        rightGrad = createDivide("", rightGrad, node.getRight());
        rightGrad = createMultiply("", grad, rightGrad);

        leftGrad = createReduceSum(leftGradName, leftGrad, pair.getLeft());
        rightGrad = createReduceSum(rightGradName, rightGrad, pair.getRight());

        node.getLeft().setGradient(node, leftGrad);
        node.getRight().setGradient(node, rightGrad);

        node.getLeft().accept(this, leftGrad);
        node.getRight().accept(this, rightGrad);
    }

    @Override
    public void visitMatMul(MatMul node, Object... params) {
        Expression grad = getGradientOrDefault(node, params);
        String leftGradName = node.getName() + "/grad_" + node.getLeft().getName();
        String rightGradName = node.getName() + "/grad_" + node.getRight().getName();

        Expression leftGrad = ExpressionFactory.createMatMul(leftGradName, grad, node.getRight(), false, true);
        Expression rightGrad = ExpressionFactory.createMatMul(rightGradName, node.getLeft(), grad, true, false);

        node.getLeft().setGradient(node, leftGrad);
        node.getRight().setGradient(node, rightGrad);

        node.getLeft().accept(this, leftGrad);
        node.getRight().accept(this, rightGrad);
    }

    @Override
    public void visitMultiply(Multiply node, Object... params) {
        Expression grad = getGradientOrDefault(node, params);

        String leftGradName = node.getName() + "/grad_" + node.getLeft().getName();
        String rightGradName = node.getName() + "/grad_" + node.getRight().getName();

        val pair = ShapeUtils.getReductionIndices(node.getLeft().getShape(), node.getRight().getShape());

        Expression leftGrad = createMultiply(leftGradName, grad, node.getRight());
        Expression rightGrad = createMultiply(rightGradName, node.getLeft(), grad);

        leftGrad = createReduceSum(leftGradName, leftGrad, pair.getLeft());
        rightGrad = createReduceSum(rightGradName, rightGrad, pair.getRight());

        node.getLeft().setGradient(node, leftGrad);
        node.getRight().setGradient(node, rightGrad);

        node.getLeft().accept(this, leftGrad);
        node.getRight().accept(this, rightGrad);
    }

    @Override
    public void visitReduceMean(ReduceMean node, Object... params) {
        Expression grad = getGradientOrDefault(node, params);
        node.getBase().setGradient(node, grad);
        node.getBase().accept(this, grad);
    }

    @Override
    public void visitReduceSum(ReduceSum node, Object... params) {
        Expression grad = getGradientOrDefault(node, params);
        node.getBase().setGradient(node, grad);
        node.getBase().accept(this, grad);
    }

    @Override
    public void visitSigmoid(Sigmoid node, Object... params) {
        Expression grad = getGradientOrDefault(node, params);

        String gradName = node.getName() + "/grad_" + node.getBase().getName();
        String sigGradName = gradName + "/sigGrad";

        Expression sigGrad = ExpressionFactory.createSigmoidGrad(sigGradName, node.getBase());
        Expression result = createMultiply(gradName, grad, sigGrad);

        node.getBase().setGradient(node, result);

        node.getBase().accept(this, result);
    }

    @Override
    public void visitSquare(Square node, Object... params) {
        Expression grad = getGradientOrDefault(node, params);

        String gradName = node.getName() + "/grad_" + node.getBase().getName();

        String mulName = gradName + "/mul";
        Expression mul = createMultiply(mulName, node.getBase(), Constant.TWO);

        Expression result = createMultiply(gradName, grad, mul);

        node.getBase().setGradient(node, result);

        node.getBase().accept(this, result);
    }

    @Override
    public void visitSubtract(Subtract node, Object... params) {
        Expression grad = getGradientOrDefault(node, params);

        String leftGradName = node.getName() + "/grad_" + node.getLeft().getName();
        String rightGradName = node.getName() + "/grad_" + node.getRight().getName();

        val pair = ShapeUtils.getReductionIndices(node.getLeft().getShape(), node.getRight().getShape());

        Expression leftGrad = createReduceSum(leftGradName, grad, pair.getLeft());
        Expression rightGrad = createReduceSum(rightGradName, grad, pair.getRight());
        rightGrad = ExpressionFactory.createNegate(rightGradName, rightGrad);

        node.getLeft().setGradient(node, leftGrad);
        node.getRight().setGradient(node, rightGrad);

        node.getLeft().accept(this, leftGrad);
        node.getRight().accept(this, rightGrad);
    }

    @Override
    public void visitRelu(Relu node, Object... params) {
        Expression grad = getGradientOrDefault(node, params);

        String gradName = node.getName() + "/grad_" + node.getBase().getName();
        String stepName = gradName + "/step";

        Expression step = ExpressionFactory.createStep(stepName, node.getBase());
        Expression result = createMultiply(gradName, grad, step);

        node.getBase().setGradient(node, result);

        node.getBase().accept(this, result);
    }

    private static Expression getGradientOrDefault(Expression node, Object... params) {
        if (params.length > 0) {
            return (Expression) params[0];
        }

        return new Fill("FILL", 1, node.getShape());
    }

}
