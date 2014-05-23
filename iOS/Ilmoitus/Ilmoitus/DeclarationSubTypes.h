//
//  DeclarationSubTypes.h
//  Ilmoitus
//
//  Created by Administrator on 23/05/14.
//  Copyright (c) 2014 42IN12EWa. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface DeclarationSubTypes : NSObject
// has error
// property with 'retain (or strong) attribute must be of object type'
@property (strong, nonatomic) NSInteger *SubTypeId;
@property (strong, nonatomic) NSMutableString *SubTypeDescription;


@end
