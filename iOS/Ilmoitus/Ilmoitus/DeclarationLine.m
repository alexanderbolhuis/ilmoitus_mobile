//
//  DeclarationLine.m
//  Ilmoitus
//
//  Created by Sjors Boom on 24/04/14.
//  Copyright (c) 2014 42IN12EWa. All rights reserved.
//

#import "DeclarationLine.h"

@implementation DeclarationLine

- (instancetype)initWithDate:(NSString *)date SubType:(NSString *)subtype Cost:(float)cost
{
    self = [super init];
    if (self)
    {
        self.cost = cost;
        self.subtype = subtype;
        self.date = date;
    }
    
    return self;
}

@end