package dev.polaris_light.constructionwand.api;

public interface IWandCore extends IWandUpgrade
{
    int getColor();

    IWandAction getWandAction();
}
