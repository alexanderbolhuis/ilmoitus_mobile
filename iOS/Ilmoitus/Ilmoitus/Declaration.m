//
//  Declaration.m
//  Ilmoitus
//
//  Created by Alexander Bolhuis on 24-04-14.
//  Copyright (c) 2014 42IN12EWa. All rights reserved.
//

#import "Declaration.h"

@implementation Declaration

- (instancetype)init
{
    self = [super init];
    if (self) {
        _attachments = [[NSMutableArray alloc]init];
        _lines = [[NSMutableArray alloc]init];
    }
    return self;
}

@end
