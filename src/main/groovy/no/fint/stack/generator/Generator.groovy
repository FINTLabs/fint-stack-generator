package no.fint.stack.generator

interface Generator {
    boolean portRequired()
    String generate(StackModel model) throws Exception
}